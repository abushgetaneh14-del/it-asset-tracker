
package com.project.it_asset_tracker.controller;

import com.project.it_asset_tracker.dto.LoginRequest;
import com.project.it_asset_tracker.dto.RegisterRequest;
import com.project.it_asset_tracker.entity.User;
import com.project.it_asset_tracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        Map<String, Object> response = Map.of(
                "message", "User registered successfully",
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.login(request);

        Map<String, Object> response = Map.of(
                "message", "Login successful",
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}
