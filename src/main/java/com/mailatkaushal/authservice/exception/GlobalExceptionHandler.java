package com.mailatkaushal.authservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mailatkaushal.authservice.service.CookieService;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice 
@RequiredArgsConstructor 
public class GlobalExceptionHandler {
    private final CookieService cookieService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail=ex.getBody();
        
        Map<String,String> errors=new HashMap<>();
        ex.getBindingResult()
            .getFieldErrors().stream()
                .forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        problemDetail.setProperty("errors",errors);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(problemDetail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler(TokenReuseException.class)
    public ResponseEntity<ProblemDetail> handleTokenReuse(TokenReuseException ex) {
        ResponseStatus responseStatus=ex.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus status=responseStatus.value();

        return ResponseEntity
                .status(status)
                .header(HttpHeaders.SET_COOKIE, cookieService.removeRefreshTokenCookie().toString())
                .body(ProblemDetail.forStatusAndDetail(status, ex.getMessage()));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ProblemDetail handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        ResponseStatus responseStatus=ex.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus status=responseStatus.value();

        return ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,ex.getMessage());
    }

}
