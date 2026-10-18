package com.mailatkaushal.authservice.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.mailatkaushal.authservice.config.JwtProperties;
import com.mailatkaushal.authservice.enums.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component 
public class JwtService {
    public static final String TYPE_CLAIM="typ";

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;
    
    public JwtService(JwtProperties properties) {
        this.jwtProperties=properties;
        byte[] keyBytes=properties.getSecret().getBytes(StandardCharsets.UTF_8);
        signingKey=Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username) {
        return buildToken(username, TokenType.ACCESS, jwtProperties.getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, TokenType.REFRESH, jwtProperties.getRefreshTokenExpirationMs());
    }

    private String buildToken(String username, TokenType type, long ttlMs) {
        Instant now=Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(TYPE_CLAIM, type.name())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ttlMs)))
                .signWith(signingKey)
                .compact(); 
    }
    
    public String validateAccessToken(String token) {
        return validate(token,TokenType.ACCESS);
    }

    public String validateRefreshToken(String token) {
        return validate(token,TokenType.REFRESH);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private String validate(String token, TokenType expectedType) {
        try {
            Claims claims=parseClaims(token);
            if (expectedType.name().equals(claims.get(TYPE_CLAIM, String.class))) return claims.getSubject();
            throw new BadCredentialsException("Invalid token type. Expected " + expectedType.name());
        } catch (IllegalArgumentException ex) {
            throw new BadCredentialsException("Token is empty or malformed", ex);
        } catch (JwtException ex) {
            throw new BadCredentialsException("Invalid or expired token", ex);
        }
    }
    
}
