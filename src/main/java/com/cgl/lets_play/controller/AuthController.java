package com.cgl.lets_play.controller;

import com.cgl.lets_play.dto.AuthRequest;
import com.cgl.lets_play.dto.AuthResponse;
import com.cgl.lets_play.dto.UserDto;
import com.cgl.lets_play.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate a user and return a JWT token")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user with the provided credentials")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(authService.register(userDto), HttpStatus.CREATED);
    }
}
