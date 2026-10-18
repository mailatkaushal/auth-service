package com.mailatkaushal.authservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mailatkaushal.authservice.entity.UserRole;
import com.mailatkaushal.authservice.enums.Role;

public interface UserRoleRepository extends JpaRepository<UserRole,UUID> {
    boolean existsByName(Role name);
    Optional<UserRole> findByName(Role role);
}
