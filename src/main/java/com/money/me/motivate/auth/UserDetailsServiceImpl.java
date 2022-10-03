package com.money.me.motivate.auth;

import com.money.me.motivate.domain.AppUser;
import com.money.me.motivate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("User with username '%s' not found", username))
                );
        Set<GrantedAuthority> permissions = appUser.getRoles().stream()
                .flatMap((role) -> role.getName().getGrantedAuthorities().stream())
                .collect(Collectors.toSet());
        return new UserDetailsImpl(
                appUser.getUsername(),
                appUser.getPassword(),
                permissions,
                true,
                true,
                true,
                true);

    }
}
