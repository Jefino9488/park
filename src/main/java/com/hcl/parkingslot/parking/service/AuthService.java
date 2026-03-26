package com.hcl.parkingslot.parking.service;

import com.hcl.parkingslot.parking.dto.AuthResponse;
import com.hcl.parkingslot.parking.dto.LoginRequest;
import com.hcl.parkingslot.parking.dto.RegisterRequest;
import com.hcl.parkingslot.parking.entity.AppUser;
import com.hcl.parkingslot.parking.enums.UserRole;
import com.hcl.parkingslot.parking.repository.AppUserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse registerUser(RegisterRequest request) {
        return register(request, UserRole.USER);
    }

    public AuthResponse registerAdmin(RegisterRequest request) {
        return register(request, UserRole.ADMIN);
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());
        if (!passwordMatches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getId(), user.getUsername(), user.getRole(), token, "Login successful");
    }

    private AuthResponse register(RegisterRequest request, UserRole role) {
        if (request.username() == null || request.username().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (request.fullName() == null || request.fullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name is required");
        }
        if (appUserRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        AppUser user = AppUser.builder()
                .fullName(request.fullName())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        AppUser saved = appUserRepository.save(user);
        String token = jwtService.generateToken(saved);
        return new AuthResponse(saved.getId(), saved.getUsername(), saved.getRole(), token, "Registration successful");
    }
}