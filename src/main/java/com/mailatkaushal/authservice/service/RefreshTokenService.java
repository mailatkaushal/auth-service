package com.mailatkaushal.authservice.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mailatkaushal.authservice.config.JwtProperties;
import com.mailatkaushal.authservice.entity.RefreshToken;
import com.mailatkaushal.authservice.entity.User;
import com.mailatkaushal.authservice.repository.RefreshTokenRepository;
import com.mailatkaushal.authservice.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class RefreshTokenService {
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(User user) {
        String refreshTokenString=jwtService.generateRefreshToken(user.getUsername());
        Instant expiryDate=Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs());

        RefreshToken refreshToken=RefreshToken.builder()
                .token(refreshTokenString)
                .expiryDate(expiryDate)
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshTokenString;
    }

    public RefreshToken findByToken(String refreshTokenString) {
        return refreshTokenRepository.findByToken(refreshTokenString);
    }

    @Transactional
    public void revokeRefreshToken(String refreshTokenString) {
        refreshTokenRepository.revokeIfActive(refreshTokenString);
    }
}
