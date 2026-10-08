package com.example.smartexpensetracker.service;

import com.example.smartexpensetracker.config.AuthInterceptor;
import com.example.smartexpensetracker.dto.LoginRequest;
import com.example.smartexpensetracker.dto.RegisterRequest;
import com.example.smartexpensetracker.dto.UserResponse;
import com.example.smartexpensetracker.entity.User;
import com.example.smartexpensetracker.exception.BadRequestException;
import com.example.smartexpensetracker.exception.UnauthorizedException;
import com.example.smartexpensetracker.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user registration, authentication, and session state.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with BCrypt password hashing and duplicate prevention.
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // Prevent duplicate username
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new BadRequestException("Username '" + request.getUsername().trim() + "' is already taken");
        }

        // Prevent duplicate email
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email '" + request.getEmail().trim().toLowerCase() + "' is already registered");
        }

        // Hash password securely with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUsername().trim(),
                request.getEmail().trim().toLowerCase(),
                hashedPassword
        );

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getCreatedAt()
        );
    }

    /**
     * Authenticates credentials and binds the user ID to the HTTP session.
     */
    public UserResponse login(LoginRequest request, HttpSession session) {
        String identifier = request.getUsernameOrEmail().trim();

        User user = userRepository.findByUsernameOrEmail(identifier, identifier.toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid username/email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username/email or password");
        }

        // Maintain user session
        session.setAttribute(AuthInterceptor.SESSION_USER_ID, user.getId());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    /**
     * Invalidates the active HTTP session.
     */
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     */
    public UserResponse getCurrentUser(HttpSession session) {
        Long userId = getAuthenticatedUserId(session);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User profile not found. Please log in again."));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    /**
     * Helper to extract authenticated user ID from HTTP session.
     */
    public Long getAuthenticatedUserId(HttpSession session) {
        if (session == null || session.getAttribute(AuthInterceptor.SESSION_USER_ID) == null) {
            throw new UnauthorizedException("Authentication required. Please log in.");
        }
        return (Long) session.getAttribute(AuthInterceptor.SESSION_USER_ID);
    }
}
