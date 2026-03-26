package com.hcl.parkingslot.parking.slot.controller;

import com.hcl.parkingslot.parking.enums.SlotStatus;
import com.hcl.parkingslot.parking.enums.SlotType;
import com.hcl.parkingslot.parking.slot.dto.CreateSlotRequest;
import com.hcl.parkingslot.parking.slot.dto.SlotResponse;
import com.hcl.parkingslot.parking.slot.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Tag(name = "Slots", description = "Parking slot management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SlotController {

    private final SlotService slotService;

    @GetMapping
    @Operation(summary = "Get slots", description = "Returns all slots or filters by status and type, for example status=AVAILABLE&type=CAR")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Slots fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SlotResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid status or type value")
    })
    public List<SlotResponse> getSlots(
            @Parameter(description = "Filter by slot status", example = "AVAILABLE")
            @RequestParam(required = false) SlotStatus status,

            @Parameter(description = "Filter by slot type", example = "CAR")
            @RequestParam(required = false) SlotType type
    ) {
        return slotService.findSlots(status, type);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get slot by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Slot fetched successfully", content = @Content(schema = @Schema(implementation = SlotResponse.class))),
            @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    public SlotResponse getSlotById(
            @Parameter(description = "Slot id", example = "10")
            @PathVariable Long id
    ) {
        return slotService.getSlotById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Slot created successfully", content = @Content(schema = @Schema(implementation = SlotResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Slot number already exists")
    })
    public SlotResponse createSlot(
            @RequestBody(description = "Create slot payload", required = true, content = @Content(schema = @Schema(implementation = CreateSlotRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateSlotRequest request
    ) {
        return slotService.createSlot(request);
    }
}
