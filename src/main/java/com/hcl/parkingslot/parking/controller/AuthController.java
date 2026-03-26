package com.hcl.parkingslot.parking.controller;

import com.hcl.parkingslot.parking.dto.AuthResponse;
import com.hcl.parkingslot.parking.dto.LoginRequest;
import com.hcl.parkingslot.parking.dto.RegisterRequest;
import com.hcl.parkingslot.parking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User and admin registration/login APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/user")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register parking user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public AuthResponse registerUser(@RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/register/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register admin user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public AuthResponse registerAdmin(@RequestBody RegisterRequest request) {
        return authService.registerAdmin(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}