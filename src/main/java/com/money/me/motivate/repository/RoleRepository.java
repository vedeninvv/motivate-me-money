package com.money.me.motivate.repository;

import com.money.me.motivate.auth.AppUserRole;
import com.money.me.motivate.domain.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(AppUserRole name);
}
