package com.hcl.parkingslot.parking.dto;

import com.hcl.parkingslot.parking.enums.UserRole;

public record AuthResponse(Long userId, String username, UserRole role, String token, String message) {
}