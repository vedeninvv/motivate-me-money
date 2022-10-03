package com.money.me.motivate.mapstruct.dto.user;

import lombok.Data;

import java.util.Set;

@Data
public class UserGetDto {
    private Long id;
    private String username;
    private Set<String> roles;
    private Double balance;
    private Double coinsTaskModifier;
    private Double coinsPerHour;
}
