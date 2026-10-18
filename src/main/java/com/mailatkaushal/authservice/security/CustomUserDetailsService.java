package com.mailatkaushal.authservice.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.mailatkaushal.authservice.entity.User;
import com.mailatkaushal.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user=userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: "+username));

        return new CustomUserDetails(user);
    }

}
