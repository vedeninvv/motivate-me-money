package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.domain.Item;
import com.money.me.motivate.mapstruct.dto.item.ItemGetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemGetDto toDto(Item item);
}
