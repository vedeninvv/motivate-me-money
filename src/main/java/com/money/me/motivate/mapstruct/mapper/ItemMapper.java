package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.domain.Item;
import com.money.me.motivate.mapstruct.dto.item.ItemGetDto;
import com.money.me.motivate.mapstruct.dto.item.ItemPostDto;
import com.money.me.motivate.mapstruct.dto.item.ItemWithAmountGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ItemModifiersSetMapper.class})
public interface ItemMapper {
    ItemWithAmountGetDto toDtoWithAmount(Item item);

    Item toModel(ItemPostDto itemPostDto);

    ItemGetDto toDto(Item item);

    void updateModel(ItemPostDto itemPostDto, @MappingTarget Item item);

    Iterable<ItemGetDto> toDtoList(Iterable<Item> items);
}
