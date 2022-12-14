package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.domain.modifiers.AppUserModifiersSet;
import com.money.me.motivate.mapstruct.dto.modifiersSet.ModifiersSetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserModifiersSetMapper {
    AppUserModifiersSet toModel(ModifiersSetDto modifiersSetDto);
    ModifiersSetDto toDto(AppUserModifiersSet appUserModifiersSet);
}
