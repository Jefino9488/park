package com.hcl.parkingslot.parking.access.dto;

import com.hcl.parkingslot.parking.enums.SessionStatus;
import com.hcl.parkingslot.parking.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(name = "ParkingEnterResponse", description = "Parking entry allocation details")
public record ParkingEnterResponse(
        @Schema(description = "Parking session id", example = "21")
        Long sessionId,

        @Schema(description = "Vehicle number", example = "TN01AB1234")
        String vehicleNumber,

        @Schema(description = "Vehicle type", example = "CAR")
        VehicleType vehicleType,

        @Schema(description = "Allocated slot id", example = "4")
        Long slotId,

        @Schema(description = "Allocated slot number", example = "A-104")
        String slotNumber,

        @Schema(description = "User id", example = "1")
        Long userId,

        @Schema(description = "Entry time", example = "2026-03-26T10:15:30")
        LocalDateTime entryTime,

        @Schema(description = "Session status", example = "ACTIVE")
        SessionStatus sessionStatus
) {
}
