package com.money.me.motivate.mapstruct.dto.user;

import com.money.me.motivate.mapstruct.dto.modifiersSet.ModifiersSetDto;
import lombok.Data;

import java.util.Set;

@Data
public class UserGetDto {
    private Long id;
    private String username;
    private Set<String> roles;
    private Double balance;
    private ModifiersSetDto modifiersSet;
}
