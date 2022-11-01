package com.money.me.motivate.domain.user;

import com.money.me.motivate.domain.user.AppUserPermission;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.money.me.motivate.domain.user.AppUserPermission.*;

public enum AppUserRole {
    ADMIN(Set.of(USER_READ, USER_WRITE, USER_DELETE, ADMIN_READ, ADMIN_WRITE, ITEM_READ, ITEM_WRITE), 1L),
    USER(Set.of(USER_READ, USER_WRITE, ADMIN_READ, OWN_TASK_WRITE, ALL_TASK_READ, ITEM_READ), 2L);

    private final Set<AppUserPermission> permissions;
    private final Long id;

    AppUserRole(Set<AppUserPermission> permissions, Long id) {
        this.permissions = permissions;
        this.id = id;
    }

    public Set<AppUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }

    public Long getId() {
        return id;
    }
}
