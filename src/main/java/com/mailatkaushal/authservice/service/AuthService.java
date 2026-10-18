package com.mailatkaushal.authservice.service;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mailatkaushal.authservice.dto.internal.AuthTokenPair;
import com.mailatkaushal.authservice.dto.request.AuthRequest;
import com.mailatkaushal.authservice.dto.request.CreateUserRequest;
import com.mailatkaushal.authservice.entity.RefreshToken;
import com.mailatkaushal.authservice.entity.User;
import com.mailatkaushal.authservice.entity.UserRole;
import com.mailatkaushal.authservice.enums.Role;
import com.mailatkaushal.authservice.exception.TokenReuseException;
import com.mailatkaushal.authservice.exception.UsernameAlreadyExistsException;
import com.mailatkaushal.authservice.repository.RefreshTokenRepository;
import com.mailatkaushal.authservice.repository.UserRepository;
import com.mailatkaushal.authservice.repository.UserRoleRepository;
import com.mailatkaushal.authservice.security.CustomUserDetails;
import com.mailatkaushal.authservice.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional 
    public AuthTokenPair signup(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException();
        }

        UserRole defaultRole=userRoleRepository.findByName(Role.USER)
                .orElseThrow(() -> new IllegalStateException("role not found in database: " + Role.USER));

        User user=User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(defaultRole))
                .build();

        user=userRepository.save(user);

        return createAuthTokenPair(user);
    }

    public AuthTokenPair login(AuthRequest request) {
        Authentication authentication=authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(),request.password())
        );
        User user=((CustomUserDetails) authentication.getPrincipal()).getUser();
        return createAuthTokenPair(user);
	}

    private AuthTokenPair createAuthTokenPair(User user) {
        String accessToken=jwtService.generateAccessToken(user.getUsername());
        String refreshToken=refreshTokenService.createRefreshToken(user);

        return new AuthTokenPair(accessToken, refreshToken);
    }

    @Transactional(noRollbackFor = TokenReuseException.class)
    public AuthTokenPair rotateRefreshToken(String refreshTokenString) {
        jwtService.validateRefreshToken(refreshTokenString);
        RefreshToken refreshToken=refreshTokenService.findByToken(refreshTokenString);

        User user=refreshToken.getUser();

        if (refreshTokenRepository.revokeIfActive(refreshTokenString) == 0) {
            refreshTokenRepository.revokeAllByUser(user);
            throw new TokenReuseException();
        }

        return createAuthTokenPair(user);
    }

}
