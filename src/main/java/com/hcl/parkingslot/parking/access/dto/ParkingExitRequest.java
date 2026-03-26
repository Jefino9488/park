package com.hcl.parkingslot.parking.access.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ParkingExitRequest", description = "Payload to exit a parked vehicle")
public record ParkingExitRequest(
        @Schema(description = "Vehicle registration number", example = "TN01AB1234", requiredMode = Schema.RequiredMode.REQUIRED)
        String vehicleNumber
) {
}
