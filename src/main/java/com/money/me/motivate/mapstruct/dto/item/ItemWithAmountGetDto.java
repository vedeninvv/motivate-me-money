package com.money.me.motivate.mapstruct.dto.item;

import com.money.me.motivate.mapstruct.dto.modifiersSet.ModifiersSetDto;
import lombok.Data;

@Data
public class ItemWithAmountGetDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    ModifiersSetDto modifiersSet;
    private Integer amount;
}
