package com.money.me.motivate.controller;

import com.money.me.motivate.mapstruct.dto.task.TaskGetDto;
import com.money.me.motivate.mapstruct.dto.task.TaskPostUpdateDto;
import com.money.me.motivate.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
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
            @ApiResponse(responseCode = "500", description = "Current user must be in database, but it's not",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)})
    @PostMapping
    @PreAuthorize("hasAuthority('ownTask:write')")
    public TaskGetDto create(@RequestBody @Valid TaskPostUpdateDto taskPostUpdateDto, Principal principal) {
        try {
            return taskService.createTask(taskPostUpdateDto, principal.getName());
        } catch (UsernameNotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server error: username must be in the database, but it's not. Exception message: " + exception.getMessage()
            );
        }
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
    public TaskGetDto update(@PathVariable Long taskId, @RequestBody @Valid TaskPostUpdateDto taskPostUpdateDto, Principal principal) {
        return taskService.updateTask(taskId, taskPostUpdateDto, principal.getName());
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
    public TaskGetDto completeTask(@PathVariable Long taskId, Principal principal) {
        return taskService.completeTask(taskId, principal.getName());
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
    public TaskGetDto deleteTask(@PathVariable Long taskId, Principal principal) {
        return taskService.deleteTask(taskId, principal.getName());
    }
}
