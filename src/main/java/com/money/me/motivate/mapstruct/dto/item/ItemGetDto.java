package com.money.me.motivate.mapstruct.dto.item;

import lombok.Data;

@Data
public class ItemGetDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double coinsTaskModifier;
    private Double coinsPerHour;
    private Integer amount;
}
