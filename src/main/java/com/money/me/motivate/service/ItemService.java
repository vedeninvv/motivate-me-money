package com.money.me.motivate.service;

import com.money.me.motivate.domain.AppUser;
import com.money.me.motivate.domain.Item;
import com.money.me.motivate.exception.NotFoundException;
import com.money.me.motivate.mapstruct.dto.item.ItemGetDto;
import com.money.me.motivate.mapstruct.dto.item.ItemPostDto;
import com.money.me.motivate.mapstruct.dto.item.ItemWithAmountGetDto;
import com.money.me.motivate.mapstruct.mapper.ItemMapper;
import com.money.me.motivate.repository.AppUserItemRepository;
import com.money.me.motivate.repository.ItemRepository;
import com.money.me.motivate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final AppUserItemRepository appUserItemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       AppUserItemRepository appUserItemRepository,
                       UserService userService,
                       ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.appUserItemRepository = appUserItemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    public Iterable<Item> findAllItems() {
        return itemRepository.findAll();
    }

    //Цена покупки может изменяться в зависимости от кол-ва уже купленных айтемов, поэтому передается user. Но пока так.
    public double calculatePrice(Item item, int amount, AppUser user) {
        return item.getBasePrice() * amount;
    }

    public ItemWithAmountGetDto buyItem(Long itemId, Integer amount, String username) {
        AppUser user = userService.getAppUserByUsername(username);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(
                    String.format("Item with id '%d' not found when user with username '%s' tried to buy it", itemId, username));
        });

        if (amount == null) {
            amount = 1;
        }
        double price = calculatePrice(item, amount, user);
        userService.changeBalance(user, user.getBalance() - price);
        int newAmount = userService.addItem(user, item, amount);
        ItemWithAmountGetDto itemWithAmountGetDto = itemMapper.toDtoWithAmount(item);
        itemWithAmountGetDto.setAmount(newAmount);
        return itemWithAmountGetDto;
    }

    @Scheduled(cron = "@hourly")
    @Transactional
    public void addCoinsToBalanceFromItemsPerHour() {
        userRepository.addCoinsToBalanceFromItemsPerHour();
    }

    public Iterable<ItemWithAmountGetDto> getAllItemsWithAmount(String username) {
        AppUser user = userService.getAppUserByUsername(username);
        Map<Item, Integer> itemsAmount = new HashMap<>();
        Iterable<Item> allItems = findAllItems();
        for (Item item : allItems) {
            itemsAmount.put(item, 0);
        }

        //Изменение количества айтемов на то, каким владеет пользователь
        appUserItemRepository.findAllByIdAppUserId(user.getId())
                .forEach(appUserItem -> {
                    itemsAmount.replace(appUserItem.getItem(), appUserItem.getAmount());
                });

        List<ItemWithAmountGetDto> itemWithAmountGetDtoList = new ArrayList<>();
        for (Item item : itemsAmount.keySet()) {
            ItemWithAmountGetDto itemWithAmountGetDto = itemMapper.toDtoWithAmount(item);
            itemWithAmountGetDto.setAmount(itemsAmount.get(item));
            itemWithAmountGetDto.setPrice(calculatePrice(item, 1, user));
            itemWithAmountGetDtoList.add(itemWithAmountGetDto);
        }
        return itemWithAmountGetDtoList;
    }

    public ItemGetDto createItem(ItemPostDto itemPostDto) {
        return itemMapper.toDto(
                itemRepository.save(itemMapper.toModel(itemPostDto))
        );
    }

    public ItemGetDto updateItem(Long itemId, ItemPostDto itemPostDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                    throw new NotFoundException(String.format("Item with id '%d' not found to update", itemId));
                }
        );
        itemMapper.updateModel(itemPostDto, item);
        itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    public ItemGetDto deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Item with id '%d' not found to delete", itemId));
                });
        itemRepository.delete(item);
        return itemMapper.toDto(item);
    }

    public Iterable<ItemGetDto> getAllItems() {
        return itemMapper.toDtoList(itemRepository.findAll());
    }
}
