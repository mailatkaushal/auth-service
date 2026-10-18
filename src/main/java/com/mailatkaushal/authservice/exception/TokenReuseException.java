package com.mailatkaushal.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenReuseException extends RuntimeException {
    public TokenReuseException() {
        super("Reused refresh token detected. All sessions revoked.");
    }
}
