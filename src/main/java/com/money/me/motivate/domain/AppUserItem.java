package com.money.me.motivate.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "appuser_item")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AppUserItem {
    @EmbeddedId
    private AppUserItemKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("appUserId")
    @JoinColumn(name = "appuser_id")
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    int amount;

    public AppUserItem(AppUser appUser, Item item) {
        this.appUser = appUser;
        this.item = item;
        this.id = new AppUserItemKey(appUser.getId(), item.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserItem that = (AppUserItem) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
