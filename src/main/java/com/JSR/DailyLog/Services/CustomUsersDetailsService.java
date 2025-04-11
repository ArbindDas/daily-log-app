package com.JSR.DailyLog.Services;

import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Repository.UsersDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomUsersDetailsService implements UserDetailsService {

    @Autowired
    private UsersDetailsRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = repository.findByUsername(username);
        if (users != null) {
            // Map roles to GrantedAuthority, ensuring the ROLE_ prefix is added if not already present
            List < GrantedAuthority > authorities = users.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority (role)) // ROLE_ prefix is handled in RoleListConverter
                    .collect( Collectors.toList());

            return org.springframework.security.core.userdetails.User.builder()
                    .username(users.getUsername())
                    .password(users.getPassword())
                    .authorities(authorities)  // Set authorities directly, not roles
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username " + username);
    }

}
