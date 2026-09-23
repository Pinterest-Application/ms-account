package com.example.msaccount.controller;

import com.example.msaccount.dto.UpdateUserRequest;
import com.example.msaccount.service.KeycloakUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    private final KeycloakUserService keycloakUserService;

    public MainController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @PatchMapping
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateUserRequest request) {

        keycloakUserService.updateUser(jwt.getSubject(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        keycloakUserService.deleteUser(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@AuthenticationPrincipal Jwt jwt) {
        keycloakUserService.logoutUser(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateUser(@AuthenticationPrincipal Jwt jwt) {
        keycloakUserService.deactivateUser(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World");
    }
}