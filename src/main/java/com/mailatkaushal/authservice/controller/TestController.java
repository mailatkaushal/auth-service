package com.mailatkaushal.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<String> publicAccess() {
        return ResponseEntity.ok("Public Page");
    }

    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> authenticatedAccess() {
        return ResponseEntity.ok("Authenticated");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userRoleAccess() {
        return ResponseEntity.ok("User Page");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminRoleAccess() {
        return ResponseEntity.ok("Admin Page");
    }
}