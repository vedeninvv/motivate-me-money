package com.money.me.motivate.controller;

import com.money.me.motivate.domain.user.AppUser;
import com.money.me.motivate.domain.user.AppUserRole;
import com.money.me.motivate.mapstruct.dto.user.UserGetDto;
import com.money.me.motivate.mapstruct.dto.user.UserPostDto;
import com.money.me.motivate.mapstruct.dto.user.UserPostWithoutRolesDto;
import com.money.me.motivate.mapstruct.dto.user.UserPutDto;
import com.money.me.motivate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/users",
        produces = "application/json")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create appUser with roles",
            description = "Create a new appUser with the specified roles. Roles that currently exist: 'ADMIN' and 'USER'",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AppUser was created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserGetDto.class))}),
            @ApiResponse(responseCode = "400", description = "Request has unknown role",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Request from appUser without role 'ADMIN'",
                    content = @Content)})
    @PostMapping
    @PreAuthorize("hasAuthority('admin:write')")
    public UserGetDto create(@RequestBody @Valid UserPostDto userPostDto) {
        return userService.createAppUserWithRoles(userPostDto);
    }

    @Operation(summary = "Create appUser with role 'USER'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPostWithoutRolesDto.class))}),
    })
    @PostMapping("/user")
    public UserGetDto createUser(@RequestBody @Valid UserPostWithoutRolesDto userPostWithoutRolesDto) {
        return userService.createNewUser(userPostWithoutRolesDto);
    }

    @Operation(summary = "Update current user",
            description = "Users can update only themselves",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Old password not correct",
                    content = @Content)})
    @PutMapping
    @PreAuthorize("hasAuthority('user:write')")
    public UserGetDto update(@RequestBody @Valid UserPutDto userPutDto,
                             @Parameter(hidden = true) @AuthenticationPrincipal AppUser user) {
        return userService.updateUser(user, userPutDto);
    }

    @Operation(summary = "Delete user",
            description = "Delete user. Only admins can perform this action",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserGetDto.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:delete')")
    public UserGetDto delete(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @Operation(summary = "Get current user",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Info about current user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserGetDto.class))}),
    })
    @GetMapping("/current")
    @PreAuthorize("hasAuthority('user:read')")
    public UserGetDto currentUser(@Parameter(hidden = true) @AuthenticationPrincipal AppUser user) {
        return userService.getUserDto(user);
    }

    @Operation(summary = "Get all users",
            description = "Get info about all users including all roles (not only appUsers with role 'USER')",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All users",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserGetDto.class)))}),
    })
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:read')")
    public List<UserGetDto> userAll(@RequestParam(required = false) AppUserRole role) {
        if (role == null) {
            return userService.getAllUsers();
        } else {
            return userService.getAllUsersByRole(role);
        }
    }
}
