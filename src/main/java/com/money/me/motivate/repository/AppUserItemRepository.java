package com.money.me.motivate.repository;

import com.money.me.motivate.domain.AppUserItem;
import com.money.me.motivate.domain.AppUserItemKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserItemRepository extends CrudRepository<AppUserItem, AppUserItemKey> {
    List<AppUserItem> findAllByIdItemId(Long itemId);
    List<AppUserItem> findAllByIdAppUserId(Long appUserId);
}
