package com.money.me.motivate.service;

import com.money.me.motivate.domain.AppUser;
import com.money.me.motivate.domain.Item;
import com.money.me.motivate.exception.NotFoundException;
import com.money.me.motivate.mapstruct.dto.item.ItemGetDto;
import com.money.me.motivate.mapstruct.mapper.ItemMapper;
import com.money.me.motivate.repository.ItemRepository;
import com.money.me.motivate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository, UserService userService, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    public Iterable<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Double calculatePrice(Item item) {
        return item.getBasePrice();
    }

    public ItemGetDto buyItem(Long itemId, String username) {
        AppUser user = userService.getAppUserByUsername(username);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(
                    String.format("Item with id '%d' not found when user with username '%s' tried to buy it", itemId, username));
        });
        userService.changeBalance(user, user.getBalance() - calculatePrice(item));
        userService.addItem(user, item);
        ItemGetDto itemGetDto = itemMapper.toDto(item);
        itemGetDto.setAmount(1);    //todo В будущем нужно добавить покупку указанного числа айтемов
        return itemMapper.toDto(item);
    }

    @Scheduled(cron = "@hourly")
    @Transactional
    public void addCoinsToBalanceFromItemsPerHour() {
        userRepository.addCoinsToBalanceFromItemsPerHour();
    }

    public Iterable<ItemGetDto> getAllItemsWithAmount(String username) {
        AppUser user = userService.getAppUserByUsername(username);
        Map<Item, Integer> itemsAmount = new HashMap<>();
        Iterable<Item> allItems = getAllItems();
        for (Item item: allItems) {
            itemsAmount.put(item, 0);
        }
        for (Item item: user.getItems()) {
            itemsAmount.replace(item, itemsAmount.get(item) + 1);
        }
        List<ItemGetDto> itemGetDtoList = new ArrayList<>();
        for (Item item: itemsAmount.keySet()) {
            ItemGetDto itemGetDto = itemMapper.toDto(item);
            itemGetDto.setAmount(itemsAmount.get(item));
            itemGetDtoList.add(itemGetDto);
        }
        return itemGetDtoList;
    }
}
