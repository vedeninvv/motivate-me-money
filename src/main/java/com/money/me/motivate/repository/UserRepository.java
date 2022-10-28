package com.money.me.motivate.repository;

import com.money.me.motivate.domain.AppUser;
import com.money.me.motivate.domain.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAllByRoles(Role role);

    @Modifying
    @Query("update AppUser user set user.balance = user.balance + user.modifiersSet.coinsPerHour")
    void addCoinsToBalanceFromItemsPerHour();
}
