package com.money.me.motivate.mapstruct.dto.user;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserPostWithoutRolesDto {
    @Size(min = 6, max = 12)
    private String username;

    @Size(min = 8, max = 16)
    private String password;
}
