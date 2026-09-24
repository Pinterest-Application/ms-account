package com.example.msaccount.controller;

import com.example.msaccount.dto.UpdateUserRequest;
import com.example.msaccount.dto.UserResponse;
import com.example.msaccount.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal Jwt jwt) {
        UserResponse user = userService.getUserById(jwt.getSubject());

        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateUserRequest request) {

        userService.updateUser(jwt.getSubject(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateUser(@AuthenticationPrincipal Jwt jwt) {
        userService.deactivateUser(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World");
    }
}