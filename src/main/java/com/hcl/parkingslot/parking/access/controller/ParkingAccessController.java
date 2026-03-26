package com.hcl.parkingslot.parking.access.controller;

import com.hcl.parkingslot.parking.access.dto.ParkingEnterRequest;
import com.hcl.parkingslot.parking.access.dto.ParkingEnterResponse;
import com.hcl.parkingslot.parking.access.dto.ParkingExitRequest;
import com.hcl.parkingslot.parking.access.dto.ParkingExitResponse;
import com.hcl.parkingslot.parking.access.service.ParkingAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
@Tag(name = "Parking", description = "Parking entry and exit APIs")
@SecurityRequirement(name = "bearerAuth")
public class ParkingAccessController {

    private final ParkingAccessService parkingAccessService;

    @PostMapping("/enter")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enter parking", description = "Allocates an available slot and starts a parking session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parking entry completed", content = @Content(schema = @Schema(implementation = ParkingEnterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Vehicle already parked or no slot available")
    })
    public ParkingEnterResponse enter(
            @RequestBody(description = "Parking entry request", required = true, content = @Content(schema = @Schema(implementation = ParkingEnterRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody ParkingEnterRequest request
    ) {
        return parkingAccessService.enter(request);
    }

    @PostMapping("/exit")
    @Operation(summary = "Exit parking", description = "Completes an active parking session and generates bill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking exit completed", content = @Content(schema = @Schema(implementation = ParkingExitResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Vehicle or active session not found")
    })
    public ParkingExitResponse exit(
            @RequestBody(description = "Parking exit request", required = true, content = @Content(schema = @Schema(implementation = ParkingExitRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody ParkingExitRequest request
    ) {
        return parkingAccessService.exit(request);
    }
}
