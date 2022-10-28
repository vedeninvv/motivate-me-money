package com.money.me.motivate.service;

import com.money.me.motivate.domain.Task;
import com.money.me.motivate.domain.user.AppUser;
import com.money.me.motivate.exception.NotFoundException;
import com.money.me.motivate.exception.TaskAlreadyCompletedException;
import com.money.me.motivate.exception.UserNotAuthorException;
import com.money.me.motivate.mapstruct.dto.task.TaskGetDto;
import com.money.me.motivate.mapstruct.dto.task.TaskPostUpdateDto;
import com.money.me.motivate.mapstruct.mapper.TaskMapper;
import com.money.me.motivate.repository.TaskRepository;
import com.money.me.motivate.settings.GlobalSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskMapper = taskMapper;
    }

    public TaskGetDto createTask(TaskPostUpdateDto taskPostUpdateDto, AppUser user) {
        Task task = taskMapper.toModel(taskPostUpdateDto);
        task.setUser(user);
        return taskMapper.toDto(taskRepository.save(task));
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(String.format("Task with id '%d' not found", taskId)));
    }

    public List<TaskGetDto> getAllTasksByUsername(String username) {
        AppUser user = userService.getAppUserByUsername(username);
        return taskMapper.toDtoList(user.getTasks());
    }

    public List<TaskGetDto> getAllTasksByUsernameAndCompleted(String username, boolean completed) {
        AppUser user = userService.getAppUserByUsername(username);
        return taskMapper.toDtoList(taskRepository.findByUserAndCompleted(user, completed));
    }

    public Double calculateTaskAward(Task task) {
        return GlobalSettings.BASIC_TASK_AWARD
                * task.getComplexity().getTaskModifier()
                * task.getUser().getModifiersSet().getCoinsTaskModifier();
    }

    public TaskGetDto completeTask(Long taskId, AppUser user) {
        Task task = getTaskById(taskId);
        if (!task.getUser().equals(user)) {
            throw new UserNotAuthorException(
                    String.format("Con not complete task. User with username '%s' is not author for task with id '%d'", user.getUsername(), taskId)
            );
        }
        if (task.isCompleted()) {
            throw new TaskAlreadyCompletedException(
                    String.format("Con not complete task. Task with id '%d' and author '%s' has already completed", taskId, user.getUsername()));
        }
        double award = calculateTaskAward(task);
        userService.changeBalance(user, user.getBalance() + award);
        task.setCompleted(true);
        task.setReceivedAward(award);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public TaskGetDto updateTask(Long taskId, TaskPostUpdateDto taskPostUpdateDto, AppUser user) {
        Task task = getTaskById(taskId);
        if (!task.getUser().equals(user)) {
            throw new UserNotAuthorException(
                    String.format("Con not update task. User with username '%s' is not author for task with id '%d'", user.getUsername(), taskId)
            );
        }
        taskMapper.updateModel(taskPostUpdateDto, task);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public TaskGetDto deleteTask(Long taskId, AppUser user) {
        Task task = getTaskById(taskId);
        if (!task.getUser().equals(user)) {
            throw new UserNotAuthorException(
                    String.format("Con not delete task. User with username '%s' is not author for task with id '%d'", user.getUsername(), taskId)
            );
        }
        taskRepository.delete(task);
        return taskMapper.toDto(task);
    }
}
