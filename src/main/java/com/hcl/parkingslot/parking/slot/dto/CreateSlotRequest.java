package com.hcl.parkingslot.parking.slot.dto;

import com.hcl.parkingslot.parking.enums.SlotStatus;
import com.hcl.parkingslot.parking.enums.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateSlotRequest", description = "Payload to create a new parking slot")
public record CreateSlotRequest(
        @Schema(description = "Unique slot identifier", example = "A-101", requiredMode = Schema.RequiredMode.REQUIRED)
        String slotNumber,

        @Schema(description = "Slot type", example = "CAR", requiredMode = Schema.RequiredMode.REQUIRED)
        SlotType type,

        @Schema(description = "Slot status. Defaults to AVAILABLE when omitted", example = "AVAILABLE")
        SlotStatus status,

        @Schema(description = "Parking lot id that owns the slot", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long parkingLotId
) {
}
