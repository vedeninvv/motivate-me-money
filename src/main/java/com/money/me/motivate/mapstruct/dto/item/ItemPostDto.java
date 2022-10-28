package com.money.me.motivate.mapstruct.dto.item;

import com.money.me.motivate.mapstruct.dto.modifiersSet.ModifiersSetDto;
import lombok.Data;

@Data
public class ItemPostDto {
    private String name;
    private String description;
    private Double basePrice;
    ModifiersSetDto modifiersSet;
}
