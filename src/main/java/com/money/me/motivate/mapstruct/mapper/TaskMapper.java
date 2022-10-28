package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.domain.Task;
import com.money.me.motivate.mapstruct.dto.task.TaskGetDto;
import com.money.me.motivate.mapstruct.dto.task.TaskPostUpdateDto;
import com.money.me.motivate.settings.GlobalSettings;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toModel(TaskPostUpdateDto taskPostUpdateDto);

    List<TaskGetDto> toDtoList(List<Task> tasks);

    void updateModel(TaskPostUpdateDto taskPostUpdateDto, @MappingTarget Task task);

    default TaskGetDto toDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskGetDto taskGetDto = new TaskGetDto();

        taskGetDto.setId(task.getId());
        taskGetDto.setDescription(task.getDescription());
        taskGetDto.setComplexity(task.getComplexity());
        taskGetDto.setCreatedDate(task.getCreatedDate());
        taskGetDto.setCompleted(task.isCompleted());
        taskGetDto.setReceivedAward(task.getReceivedAward());

        taskGetDto.setCoinAward(
                GlobalSettings.BASIC_TASK_AWARD
                        * task.getComplexity().getTaskModifier()
                        * task.getUser().getModifiersSet().getCoinsTaskModifier()
        );
        return taskGetDto;
    }
}
