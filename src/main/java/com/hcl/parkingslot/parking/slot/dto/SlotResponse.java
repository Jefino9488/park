package com.hcl.parkingslot.parking.slot.dto;

import com.hcl.parkingslot.parking.enums.SlotStatus;
import com.hcl.parkingslot.parking.enums.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SlotResponse", description = "Parking slot details")
public record SlotResponse(
        @Schema(description = "Slot id", example = "10")
        Long id,

        @Schema(description = "Unique slot identifier", example = "A-101")
        String slotNumber,

        @Schema(description = "Slot type", example = "CAR")
        SlotType type,

        @Schema(description = "Slot status", example = "AVAILABLE")
        SlotStatus status,

        @Schema(description = "Owning parking lot id", example = "1")
        Long parkingLotId
) {
}
