package com.money.me.motivate.controller;

import com.money.me.motivate.domain.Item;
import com.money.me.motivate.mapstruct.dto.item.ItemGetDto;
import com.money.me.motivate.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/api/users/items",
        produces = "application/json"
)
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "Get all items with user's amount",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All items with user's amount",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ItemGetDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Current user must be in database, but it's not",
                    content = @Content)
    })
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('item:read')")
    public Iterable<ItemGetDto> getAll(@RequestParam String username) {
        return itemService.getAllItemsWithAmount(username);
    }

    @Operation(summary = "Buy item by id",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item was bought",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Not enough coins",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Current user must be in database, but it's not",
                    content = @Content)
    })
    @PostMapping("/{itemId}/buy")
    @PreAuthorize("hasAuthority('item:read')")
    public ItemGetDto buy(@PathVariable Long itemId, Principal principal) {
        return itemService.buyItem(itemId, principal.getName());
    }
}
