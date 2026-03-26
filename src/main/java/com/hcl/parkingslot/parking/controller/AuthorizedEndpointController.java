package com.hcl.parkingslot.parking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AuthorizedEndpointController {

    @Tag(name = "Users", description = "Authorized user endpoints")
    @GetMapping("/api/users/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user profile", description = "Requires valid JWT token")
    public Map<String, String> currentUser() {
        return Map.of("message", "Authorized user endpoint hit", "endpoint", "/api/users/me");
    }

    @Tag(name = "Admin", description = "Authorized admin endpoints")
    @GetMapping("/api/admin/overview")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get admin overview", description = "Requires valid JWT token and ADMIN role")
    public Map<String, String> adminOverview() {
        return Map.of("message", "Authorized admin endpoint hit", "endpoint", "/api/admin/overview");
    }
}