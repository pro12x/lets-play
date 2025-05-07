package com.cgl.lets_play.controller;

import com.cgl.lets_play.dto.UserDto;
import com.cgl.lets_play.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser().getId() == #id")
    @PostAuthorize("hasRole('ADMIN') or returnObject.body.id == authentication.principal.id")
    @Operation(summary = "Get user by ID", description = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current user")
    @ApiResponse(responseCode = "200", description = "Current user retrieved successfully")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.addUser(userDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser().getId() == #id")
    @Operation(summary = "Update user", description = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser().getId() == #id")
    @Operation(summary = "Delete user", description = "Delete user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
