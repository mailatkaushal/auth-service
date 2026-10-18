package com.mailatkaushal.authservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mailatkaushal.authservice.entity.RefreshToken;
import com.mailatkaushal.authservice.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,UUID> {
    RefreshToken findByToken(String refreshTokenString);

    @Modifying 
    @Transactional 
    @Query("update RefreshToken r set r.revoked=true where r.token=:token and r.revoked=false")
    int revokeIfActive(@Param("token") String token);

    
    @Modifying 
    @Transactional 
    @Query("update RefreshToken r set r.revoked=true where r.user=:user")
    void revokeAllByUser(@Param("user") User user);
}
