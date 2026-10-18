package com.mailatkaushal.authservice.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mailatkaushal.authservice.entity.UserRole;
import com.mailatkaushal.authservice.enums.Role;
import com.mailatkaushal.authservice.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Configuration 
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRoleRepository userRoleRepository;

    @Bean 
    public CommandLineRunner initRoles() {
        return args -> {
            Arrays.stream(Role.values()).forEach(role -> {
                if (!userRoleRepository.existsByName(role)) {
                    userRoleRepository.save(new UserRole(role));
                }
            });
        };
    }
}
