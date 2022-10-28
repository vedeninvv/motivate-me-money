package com.money.me.motivate.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AppUserItemKey implements Serializable {
    @Column(name = "appuser_id")
    private Long appUserId;

    @Column(name = "item_id")
    private Long itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserItemKey that = (AppUserItemKey) o;
        return appUserId.equals(that.appUserId) && itemId.equals(that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appUserId, itemId);
    }
}
