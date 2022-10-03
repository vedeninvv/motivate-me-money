package com.money.me.motivate.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.money.me.motivate.auth.AppUserPermission.*;

public enum AppUserRole {
    USER(Set.of(USER_READ, USER_WRITE, ADMIN_READ, OWN_TASK_WRITE, ALL_TASK_READ, ITEM_READ)),
    ADMIN(Set.of(USER_READ, USER_WRITE, USER_DELETE, ADMIN_READ, ADMIN_WRITE, ITEM_READ, ITEM_WRITE));

    private final Set<AppUserPermission> permissions;

    AppUserRole(Set<AppUserPermission> permissions) {
        this.permissions = permissions;
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
}
