package com.mailatkaushal.authservice.entity;

import java.util.UUID;

import com.mailatkaushal.authservice.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "roles")
@Getter 
@NoArgsConstructor 
public class UserRole {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private Role name;

    public UserRole(Role name) {
        this.name=name;
    }
}
