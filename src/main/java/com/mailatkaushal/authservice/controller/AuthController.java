package com.mailatkaushal.authservice.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailatkaushal.authservice.constant.CookieConstants;
import com.mailatkaushal.authservice.dto.internal.AuthTokenPair;
import com.mailatkaushal.authservice.dto.request.AuthRequest;
import com.mailatkaushal.authservice.dto.request.CreateUserRequest;
import com.mailatkaushal.authservice.dto.response.AuthResponse;
import com.mailatkaushal.authservice.service.AuthService;
import com.mailatkaushal.authservice.service.CookieService;
import com.mailatkaushal.authservice.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/auth")
@RequiredArgsConstructor 
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody CreateUserRequest request) {
        AuthTokenPair authTokenPair=authService.signup(request);
        return buildAuthResponse(authTokenPair);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthTokenPair authTokenPair=authService.login(request);
        return buildAuthResponse(authTokenPair);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = CookieConstants.REFRESH_TOKEN) String refreshTokenString) {
        AuthTokenPair authTokenPair=authService.rotateRefreshToken(refreshTokenString);
        return buildAuthResponse(authTokenPair);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = CookieConstants.REFRESH_TOKEN, required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        ResponseCookie emptyRefreshTokenCookie=cookieService.removeRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, emptyRefreshTokenCookie.toString())
                .build();
    }

    private ResponseEntity<AuthResponse> buildAuthResponse(AuthTokenPair authTokenPair) {
        ResponseCookie refreshTokenCookie=cookieService.createRefreshTokenCookie(authTokenPair.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new AuthResponse(authTokenPair.accessToken()));
    }
}
