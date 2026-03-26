package com.hcl.parkingslot.parking.access.dto;

import com.hcl.parkingslot.parking.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ParkingEnterRequest", description = "Payload to enter a vehicle into the parking lot")
public record ParkingEnterRequest(
        @Schema(description = "Vehicle registration number", example = "TN01AB1234", requiredMode = Schema.RequiredMode.REQUIRED)
        String vehicleNumber,

        @Schema(description = "Vehicle type", example = "CAR", requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleType vehicleType,

        @Schema(description = "User id of the vehicle owner", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId
) {
}
