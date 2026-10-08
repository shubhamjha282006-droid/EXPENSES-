package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.dto.ApiResponse;
import com.example.smartexpensetracker.dto.LoginRequest;
import com.example.smartexpensetracker.dto.RegisterRequest;
import com.example.smartexpensetracker.dto.UserResponse;
import com.example.smartexpensetracker.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling user registration, login, logout, and profile checks.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register - Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return new ResponseEntity<>(
                ApiResponse.success("User registered successfully! You can now log in.", response),
                HttpStatus.CREATED
        );
    }

    /**
     * POST /api/auth/login - Log in and initiate session.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request,
                                                           HttpSession session) {
        UserResponse response = authService.login(request, session);
        return ResponseEntity.ok(ApiResponse.success("Login successful!", response));
    }

    /**
     * POST /api/auth/logout - Invalidate user session.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully."));
    }

    /**
     * GET /api/auth/me - Verify session and get active user profile.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpSession session) {
        UserResponse response = authService.getCurrentUser(session);
        return ResponseEntity.ok(ApiResponse.success("Authenticated user retrieved.", response));
    }
}
