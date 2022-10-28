package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.domain.AppUserModifiersSet;
import com.money.me.motivate.domain.ItemModifiersSet;
import com.money.me.motivate.mapstruct.dto.modifiersSet.ModifiersSetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserModifiersSetMapper {
    AppUserModifiersSet toModel(ModifiersSetDto modifiersSetDto);
    ModifiersSetDto toDto(AppUserModifiersSet appUserModifiersSet);
}
