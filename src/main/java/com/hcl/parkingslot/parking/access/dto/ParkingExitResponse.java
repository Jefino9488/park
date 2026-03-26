package com.hcl.parkingslot.parking.access.dto;

import com.hcl.parkingslot.parking.enums.PaymentStatus;
import com.hcl.parkingslot.parking.enums.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(name = "ParkingExitResponse", description = "Parking exit and billing details")
public record ParkingExitResponse(
        @Schema(description = "Parking session id", example = "21")
        Long sessionId,

        @Schema(description = "Bill id", example = "7")
        Long billId,

        @Schema(description = "Vehicle number", example = "TN01AB1234")
        String vehicleNumber,

        @Schema(description = "Slot id", example = "4")
        Long slotId,

        @Schema(description = "Slot number", example = "A-104")
        String slotNumber,

        @Schema(description = "Entry time", example = "2026-03-26T10:15:30")
        LocalDateTime entryTime,

        @Schema(description = "Exit time", example = "2026-03-26T13:00:00")
        LocalDateTime exitTime,

        @Schema(description = "Duration in minutes", example = "165")
        Long durationInMinutes,

        @Schema(description = "Final amount", example = "247.50")
        Double amount,

        @Schema(description = "Session status", example = "COMPLETED")
        SessionStatus sessionStatus,

        @Schema(description = "Payment status", example = "PENDING")
        PaymentStatus paymentStatus
) {
}
