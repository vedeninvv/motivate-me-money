package com.money.me.motivate.mapstruct.mapper;

import com.money.me.motivate.auth.AppUserRole;
import com.money.me.motivate.domain.Role;
import com.money.me.motivate.exception.NotFoundException;
import com.money.me.motivate.repository.RoleRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {
    @Autowired
    private RoleRepository roleRepository;

    public Role toRole(String name) {
        return roleRepository.findByName(AppUserRole.valueOf(name))
                .orElseThrow(() -> new NotFoundException(String.format("Role '%s' not found", name)));
    }
}
