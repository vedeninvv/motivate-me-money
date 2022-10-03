package com.money.me.motivate.domain;

import com.money.me.motivate.settings.GlobalSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "appuser")
@Getter
@Setter
@RequiredArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private Double balance = GlobalSettings.INIT_BALANCE;
    private Double coinsTaskModifier = GlobalSettings.INIT_COINS_TASK_MODIFIER;
    private Double coinsPerHour = GlobalSettings.INIT_COINS_PER_HOUR;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "appuser_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Task> tasks = new ArrayList<>();

    @ManyToMany
    private List<Item> items = new ArrayList<>();
}
