package com.money.me.motivate.controller;

import com.money.me.motivate.domain.user.AppUser;
import com.money.me.motivate.mapstruct.dto.item.ItemGetDto;
import com.money.me.motivate.mapstruct.dto.item.ItemPostDto;
import com.money.me.motivate.mapstruct.dto.item.ItemWithAmountGetDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/api/items",
        produces = "application/json"
)
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "Create new item",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item was created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Only users with role 'ADMIN' can create new item",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemGetDto.class))})
    })
    @PostMapping
    @PreAuthorize("hasAuthority('item:write')")
    public ItemGetDto create(@RequestBody ItemPostDto itemPostDto) {
        return itemService.createItem(itemPostDto);
    }

    @Operation(summary = "Update item",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Only users with role 'ADMIN' can update item",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemGetDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemGetDto.class))})
    })
    @PutMapping("/{itemId}")
    @PreAuthorize("hasAuthority('item:write')")
    public ItemGetDto update(@PathVariable Long itemId, @RequestBody ItemPostDto itemPostDto) {
        return itemService.updateItem(itemId, itemPostDto);
    }

    @Operation(summary = "Delete item",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item was deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemWithAmountGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Only users with role 'ADMIN' can delete item",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemWithAmountGetDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemWithAmountGetDto.class))})
    })
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasAuthority('item:write')")
    public ItemGetDto delete(@PathVariable Long itemId) {
        return itemService.deleteItem(itemId);
    }

    @Operation(summary = "Get all items",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All items",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ItemGetDto.class)))})
    })
    @GetMapping()
    @PreAuthorize("hasAuthority('item:read')")
    public Iterable<ItemGetDto> getAll() {
        return itemService.getAllItems();
    }

    @Operation(summary = "Get all items with user's amount",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All items with user's amount",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ItemWithAmountGetDto.class)))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
    })
    @GetMapping("/all-with-amount")
    @PreAuthorize("hasAuthority('item:read')")
    public Iterable<ItemWithAmountGetDto> getAllWithAmount(@RequestParam String username) {
        return itemService.getAllItemsWithAmount(username);
    }

    @Operation(summary = "Buy items by id", description = "Buy items by id. if amount is not set, " +
            "then one item will be bought", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item was bought",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemWithAmountGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Not enough coins",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content)
    })
    @PostMapping("/{itemId}/buy")
    @PreAuthorize("hasAuthority('item:read')")
    public ItemWithAmountGetDto buy(@PathVariable Long itemId,
                                    @RequestParam(required = false) Integer amount,
                                    @AuthenticationPrincipal AppUser user) {
        return itemService.buyItem(itemId, amount, user);
    }
}
