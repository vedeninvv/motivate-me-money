package com.money.me.motivate.domain.user;

public enum AppUserPermission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_DELETE("user:delete"),
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    OWN_TASK_WRITE("ownTask:write"),
    ALL_TASK_WRITE("allTask:write"),
    ALL_TASK_READ("allTask:read"),
    ITEM_READ("item:read"),
    ITEM_WRITE("item:write");

    private final String permission;

    AppUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
