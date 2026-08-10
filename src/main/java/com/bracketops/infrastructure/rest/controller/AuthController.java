package com.bracketops.infrastructure.rest.controller;

import com.bracketops.domain.model.entity.UserDomain;
import com.bracketops.domain.model.exception.DomainException;
import com.bracketops.domain.model.valueobject.Role;
import com.bracketops.domain.port.outbound.UserRepositoryPort;
import com.bracketops.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User Registration & Authentication REST API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepositoryPort userRepositoryPort,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String username, @NotBlank String password, @NotBlank String fullName, String email, String role) {}
    public record AuthResponse(String token, String username, String fullName, String role) {}

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDomain user = userRepositoryPort.findByUsername(request.username())
                .orElseThrow(() -> new DomainException("User not found"));

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getFullName(), user.getRole().name()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user (ADMIN, CAPTAIN, SPECTATOR)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepositoryPort.existsByUsername(request.username())) {
            throw new DomainException("Username '" + request.username() + "' is already taken");
        }

        Role userRole = Role.ROLE_CAPTAIN;
        if (request.role() != null) {
            try {
                userRole = Role.valueOf(request.role().startsWith("ROLE_") ? request.role() : "ROLE_" + request.role().toUpperCase());
            } catch (Exception ignored) {}
        }

        UserDomain user = new UserDomain(
                null,
                request.username(),
                passwordEncoder.encode(request.password()),
                request.fullName(),
                request.email() != null ? request.email() : request.username() + "@bracketops.gg",
                userRole,
                true
        );

        UserDomain savedUser = userRepositoryPort.save(user);
        String token = tokenProvider.generateToken(savedUser.getUsername(), savedUser.getRole().name());

        return ResponseEntity.ok(new AuthResponse(token, savedUser.getUsername(), savedUser.getFullName(), savedUser.getRole().name()));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user details")
    public ResponseEntity<AuthResponse> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        UserDomain user = userRepositoryPort.findByUsername(principal.getName())
                .orElseThrow(() -> new DomainException("User not found"));
        return ResponseEntity.ok(new AuthResponse(null, user.getUsername(), user.getFullName(), user.getRole().name()));
    }
}
