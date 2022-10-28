package com.money.me.motivate.service;

import com.money.me.motivate.domain.AppUserItem;
import com.money.me.motivate.domain.AppUserItemKey;
import com.money.me.motivate.domain.Item;
import com.money.me.motivate.domain.modifiers.AppUserModifiersSet;
import com.money.me.motivate.domain.user.AppUser;
import com.money.me.motivate.domain.user.AppUserRole;
import com.money.me.motivate.domain.user.Role;
import com.money.me.motivate.exception.NegativeBalanceException;
import com.money.me.motivate.exception.NotFoundException;
import com.money.me.motivate.exception.PasswordNotCorrectException;
import com.money.me.motivate.mapstruct.dto.user.UserGetDto;
import com.money.me.motivate.mapstruct.dto.user.UserPostDto;
import com.money.me.motivate.mapstruct.dto.user.UserPutDto;
import com.money.me.motivate.mapstruct.mapper.UserMapper;
import com.money.me.motivate.repository.AppUserItemRepository;
import com.money.me.motivate.repository.RoleRepository;
import com.money.me.motivate.repository.UserRepository;
import com.money.me.motivate.settings.GlobalSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AppUserItemRepository appUserItemRepository;
    private final ModifiersSetService modifiersSetService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       AppUserItemRepository appUserItemRepository,
                       ModifiersSetService modifiersSetService,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.appUserItemRepository = appUserItemRepository;
        this.modifiersSetService = modifiersSetService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserGetDto createNewUser(UserPostDto userPostDto) {
        userPostDto.setRoles(Set.of(AppUserRole.USER.name()));
        return createAppUser(userPostDto);
    }

    public UserGetDto createNewAdmin(UserPostDto userPostDto) {
        userPostDto.setRoles(Set.of(AppUserRole.ADMIN.name()));
        return createAppUser(userPostDto);
    }

    public UserGetDto createAppUser(UserPostDto userPostDto) {
        try {
            for (String role : userPostDto.getRoles()) {
                AppUserRole.valueOf(role);
            }
        } catch (IllegalArgumentException exception) {
            throw new NotFoundException(exception.getMessage());
        }
        userPostDto.setPassword(passwordEncoder.encode(userPostDto.getPassword()));
        AppUser user = userMapper.toModel(userPostDto);
        AppUserModifiersSet modifiersSet = new AppUserModifiersSet();
        modifiersSet.setCoinsTaskModifier(GlobalSettings.INIT_COINS_TASK_MODIFIER);
        modifiersSet.setCoinsPerHour(GlobalSettings.INIT_COINS_PER_HOUR);
        user.setModifiersSet(modifiersSet);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public List<UserGetDto> getAllUsers() {
        return userMapper.toDtoList(new ArrayList<AppUser>((Collection<? extends AppUser>) userRepository.findAll()));
    }

    public List<UserGetDto> getAllAdmins() {
        Role adminRole = roleRepository.findByName(AppUserRole.valueOf("ADMIN"))
                .orElseThrow(() -> new NotFoundException("Admin role does not exist"));
        List<AppUser> appUserList = userRepository.findAllByRoles(adminRole);
        return userMapper.toDtoList(appUserList);
    }

    public UserGetDto getUserDto(AppUser user) {
        return userMapper.toDto(user);
    }

    public AppUser getAppUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Username '%s' not found", username)));
    }

    public UserGetDto updateUser(AppUser user, UserPutDto userPutDto) {
        if (!passwordEncoder.matches(userPutDto.getOldPassword(), user.getPassword())) {
            throw new PasswordNotCorrectException(String.format("Password for user '%s' not correct", user.getUsername()));
        }
        userPutDto.setPassword(passwordEncoder.encode(userPutDto.getPassword()));
        userMapper.updateModel(userPutDto, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void changeBalance(AppUser user, Double newBalance) {
        if (newBalance < 0) {
            throw new NegativeBalanceException(
                    String.format("Balance could not be negative. Current balance is '%f' and new balance is '%f'", user.getBalance(), newBalance));
        }
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    public Integer addItem(AppUser user, Item item, Integer amount) {
        modifiersSetService.sumModifiersSet(user.getModifiersSet(),
                modifiersSetService.multiplyModifiersSet(item.getModifiersSet(), amount));
        userRepository.save(user);
        Optional<AppUserItem> appUserItem = appUserItemRepository.findById(new AppUserItemKey(user.getId(), item.getId()));
        int newAmount;
        if (appUserItem.isEmpty()) {
            appUserItemRepository.save(
                    new AppUserItem(
                            new AppUserItemKey(user.getId(), item.getId()),
                            user,
                            item,
                            amount
                    ));
            newAmount = amount;
        } else {
            newAmount = appUserItem.get().getAmount() + amount;
            appUserItem.get().setAmount(newAmount);
            appUserItemRepository.save(appUserItem.get());
        }
        return newAmount;
    }

    public UserGetDto deleteUser(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id '%d' not found", userId)));
        UserGetDto userGetDto = userMapper.toDto(user);
        userRepository.delete(user);
        return userGetDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("User with username '%s' not found", username))
                );
    }
}
