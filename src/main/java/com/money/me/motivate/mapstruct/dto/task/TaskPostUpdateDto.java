package com.money.me.motivate.mapstruct.dto.task;

import com.money.me.motivate.settings.Complexity;
import lombok.Data;

@Data
public class TaskPostUpdateDto {
    private String description;
    private Complexity complexity;
}
