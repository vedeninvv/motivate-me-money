package com.money.me.motivate.repository;

import com.money.me.motivate.domain.user.AppUser;
import com.money.me.motivate.domain.user.AppUserRole;
import com.money.me.motivate.domain.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    @Query("select user from AppUser  user join user.roles role where role.name = ?1")
    List<AppUser> findAllByRole(AppUserRole role);

    @Modifying
    @Query("update AppUser user set user.balance = user.balance + user.modifiersSet.coinsPerHour")
    void addCoinsToBalanceFromItemsPerHour();
}
