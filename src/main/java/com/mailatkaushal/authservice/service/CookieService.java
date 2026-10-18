package com.mailatkaushal.authservice.service;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.mailatkaushal.authservice.config.JwtProperties;
import com.mailatkaushal.authservice.config.RefreshCookieProperties;
import com.mailatkaushal.authservice.constant.CookieConstants;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CookieService {
    private final RefreshCookieProperties refreshCookieProperties;
    private final JwtProperties jwtProperties;

    public ResponseCookie createRefreshTokenCookie(String token) {
        return baseCookie(CookieConstants.REFRESH_TOKEN, token)
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshTokenExpirationMs()))
                .build();
    }

    public ResponseCookie removeRefreshTokenCookie() {
        return baseCookie(CookieConstants.REFRESH_TOKEN, "")
                .maxAge(Duration.ZERO)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .path(refreshCookieProperties.getPath())
                .httpOnly(refreshCookieProperties.isHttpOnly())
                .secure(refreshCookieProperties.isSecure())
                .sameSite(refreshCookieProperties.getSameSite());
    }
}
