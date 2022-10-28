package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.domain.AppUser;
import com.money.me.motivate.domain.Role;
import com.money.me.motivate.mapstruct.dto.user.UserGetDto;
import com.money.me.motivate.mapstruct.dto.user.UserPostDto;
import com.money.me.motivate.mapstruct.dto.user.UserPutDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, AppUserModifiersSetMapper.class})
public interface UserMapper {
    UserGetDto toDto(AppUser appUser);

    List<UserGetDto> toDtoList(List<AppUser> appUserList);

    AppUser toModel(UserPostDto userPostDto);

    void updateModel(UserPutDto userPutDto, @MappingTarget AppUser user);

    default Set<String> map(Set<Role> value) {
        if (value == null) {
            return null;
        }
        Set<String> roleStrSet = new HashSet<>();
        for (var role : value) {
            roleStrSet.add(role.getName().name());
        }
        return roleStrSet;
    }
}
