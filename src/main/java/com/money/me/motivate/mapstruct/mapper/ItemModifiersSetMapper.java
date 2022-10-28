package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.domain.modifiers.ItemModifiersSet;
import com.money.me.motivate.mapstruct.dto.modifiersSet.ModifiersSetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemModifiersSetMapper {
    ItemModifiersSet toModel(ModifiersSetDto modifiersSetDto);
    ModifiersSetDto toDto(ItemModifiersSet ItemModifiersSet);
    void updateModel(ModifiersSetDto modifiersSetDto, @MappingTarget ItemModifiersSet itemModifiersSet);
}
