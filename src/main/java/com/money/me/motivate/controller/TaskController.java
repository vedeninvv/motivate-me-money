package com.money.me.motivate.controller;

import com.money.me.motivate.domain.user.AppUser;
import com.money.me.motivate.mapstruct.dto.task.TaskGetDto;
import com.money.me.motivate.mapstruct.dto.task.TaskPostUpdateDto;
import com.money.me.motivate.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/users/tasks",
        produces = "application/json")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Create task",
            description = "Create task. Current user will be author for it. Only users (role 'USER') can create task",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)})
    @PostMapping
    @PreAuthorize("hasAuthority('ownTask:write')")
    public TaskGetDto create(@RequestBody @Valid TaskPostUpdateDto taskPostUpdateDto,
                             @Parameter(hidden = true) @AuthenticationPrincipal AppUser user) {
        return taskService.createTask(taskPostUpdateDto, user);
    }

    @Operation(summary = "Update task",
            description = "Update task. Only author can update it's tasks",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Current user is not author",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)})
    @PutMapping("/{taskId}")
    @PreAuthorize("hasAuthority('ownTask:write')")
    public TaskGetDto update(@PathVariable Long taskId,
                             @RequestBody @Valid TaskPostUpdateDto taskPostUpdateDto,
                             @Parameter(hidden = true) @AuthenticationPrincipal AppUser user) {
        return taskService.updateTask(taskId, taskPostUpdateDto, user);
    }

    @Operation(summary = "Complete task",
            description = "Complete task. Only author can complete it's tasks. Tasks cannot be completed more than once",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was changed to completed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDto.class))}),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not author",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Task has already completed",
                    content = @Content)})
    @PostMapping("/{taskId}/complete")
    @PreAuthorize("hasAuthority('ownTask:write')")
    public TaskGetDto completeTask(@PathVariable Long taskId,
                                   @Parameter(hidden = true) @AuthenticationPrincipal AppUser user) {
        return taskService.completeTask(taskId, user);
    }

    @Operation(summary = "Get all tasks by user and current status",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All user's tasks",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskGetDto.class)))}),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content)})
    @GetMapping("/all/completed")
    @PreAuthorize("hasAuthority('allTask:read')")
    public List<TaskGetDto> getAllByUsernameAndCompleted(@RequestParam String username, @RequestParam(required = false) Boolean completed) {
        if (completed != null) {
            return taskService.getAllTasksByUsernameAndCompleted(username, completed);
        } else {
            return taskService.getAllTasksByUsername(username);
        }
    }

    @Operation(summary = "Delete task",
            description = "Delete task. Only author can delete it's tasks",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDto.class))}),
            @ApiResponse(responseCode = "403", description = "Current user is not author",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content),
    })
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAuthority('ownTask:write')")
    public TaskGetDto deleteTask(@PathVariable Long taskId,
                                 @Parameter(hidden = true) @AuthenticationPrincipal AppUser user) {
        return taskService.deleteTask(taskId, user);
    }
}
