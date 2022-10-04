package com.money.me.motivate.service;

import com.money.me.motivate.auth.AppUserRole;
import com.money.me.motivate.domain.AppUser;
import com.money.me.motivate.domain.Item;
import com.money.me.motivate.domain.Role;
import com.money.me.motivate.exception.NegativeBalanceException;
import com.money.me.motivate.exception.NotFoundException;
import com.money.me.motivate.exception.PasswordNotCorrectException;
import com.money.me.motivate.mapstruct.dto.user.UserGetDto;
import com.money.me.motivate.mapstruct.dto.user.UserPostDto;
import com.money.me.motivate.mapstruct.dto.user.UserPutDto;
import com.money.me.motivate.mapstruct.mapper.UserMapper;
import com.money.me.motivate.repository.RoleRepository;
import com.money.me.motivate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserGetDto createNewUser(UserPostDto userPostDto) {
        return createAppUserWithRole(userPostDto, AppUserRole.USER);
    }

    public UserGetDto createNewAdmin(UserPostDto userPostDto) {
        return createAppUserWithRole(userPostDto, AppUserRole.ADMIN);
    }

    public UserGetDto createAppUser(UserPostDto userPostDto) {
        try {
            for (String role: userPostDto.getRoles()) {
                AppUserRole.valueOf(role);
            }
        } catch (IllegalArgumentException exception) {
            throw new NotFoundException(exception.getMessage());
        }
        userPostDto.setPassword(passwordEncoder.encode(userPostDto.getPassword()));
        AppUser user = userRepository.save(userMapper.toModel(userPostDto));
        return userMapper.toDto(user);
    }

    public UserGetDto createAppUserWithRole(UserPostDto userPostDto, AppUserRole role) {
        userPostDto.setRoles(Set.of());
        userPostDto.setPassword(passwordEncoder.encode(userPostDto.getPassword()));
        AppUser appUser = userMapper.toModel(userPostDto);
        appUser.setRoles(Set.of(
                roleRepository.findByName(role)
                        .orElseThrow(() -> new NotFoundException("User role does not exist"))));
        appUser = userRepository.save(appUser);
        return userMapper.toDto(appUser);
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

    public UserGetDto getUserByUsername(String username) {
        return userMapper.toDto(
                getAppUserByUsername(username)
        );
    }

    public AppUser getAppUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Username '%s' not found", username)));
    }

    public UserGetDto updateUser(String username, UserPutDto userPutDto) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Username '%s' not found", username)));
        if (!passwordEncoder.matches(userPutDto.getOldPassword(), user.getPassword())) {
            throw new PasswordNotCorrectException(String.format("Password for user '%s' not correct", username));
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

    public void addItem(AppUser user, Item item) {
        user.setCoinsTaskModifier(user.getCoinsTaskModifier() + item.getCoinsTaskModifier());
        user.setCoinsPerHour(user.getCoinsPerHour() + item.getCoinsPerHour());
        user.getItems().add(item);
        userRepository.save(user);
    }

    public UserGetDto deleteUser(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id '%d' not found", userId)));
        UserGetDto userGetDto = userMapper.toDto(user);
        userRepository.delete(user);
        return userGetDto;
    }
}
