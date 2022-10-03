package com.money.me.motivate.mapstruct.dto.task;

import com.money.me.motivate.settings.Complexity;
import lombok.Data;

import java.util.Date;

@Data
public class TaskGetDto {
    private Long id;
    private String description;
    private Complexity complexity;
    private Double coinAward;
    private Date createdDate;
    private boolean completed;
}
