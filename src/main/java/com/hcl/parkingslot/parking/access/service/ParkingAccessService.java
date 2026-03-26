package com.hcl.parkingslot.parking.access.service;

import com.hcl.parkingslot.parking.access.dto.ParkingEnterRequest;
import com.hcl.parkingslot.parking.access.dto.ParkingEnterResponse;
import com.hcl.parkingslot.parking.access.dto.ParkingExitRequest;
import com.hcl.parkingslot.parking.access.dto.ParkingExitResponse;
import com.hcl.parkingslot.parking.access.repository.AppUserAccessRepository;
import com.hcl.parkingslot.parking.access.repository.BillAccessRepository;
import com.hcl.parkingslot.parking.access.repository.ParkingSessionAccessRepository;
import com.hcl.parkingslot.parking.access.repository.ParkingSlotAccessRepository;
import com.hcl.parkingslot.parking.access.repository.PricingPolicyAccessRepository;
import com.hcl.parkingslot.parking.access.repository.VehicleAccessRepository;
import com.hcl.parkingslot.parking.entity.AppUser;
import com.hcl.parkingslot.parking.entity.Bill;
import com.hcl.parkingslot.parking.entity.ParkingSession;
import com.hcl.parkingslot.parking.entity.ParkingSlot;
import com.hcl.parkingslot.parking.entity.PricingPolicy;
import com.hcl.parkingslot.parking.entity.Vehicle;
import com.hcl.parkingslot.parking.enums.PaymentStatus;
import com.hcl.parkingslot.parking.enums.SessionStatus;
import com.hcl.parkingslot.parking.enums.SlotStatus;
import com.hcl.parkingslot.parking.enums.SlotType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ParkingAccessService {

    private final AppUserAccessRepository appUserAccessRepository;
    private final VehicleAccessRepository vehicleAccessRepository;
    private final ParkingSlotAccessRepository parkingSlotAccessRepository;
    private final ParkingSessionAccessRepository parkingSessionAccessRepository;
    private final PricingPolicyAccessRepository pricingPolicyAccessRepository;
    private final BillAccessRepository billAccessRepository;

    @Transactional
    public ParkingEnterResponse enter(ParkingEnterRequest request) {
        validateEnterRequest(request);

        AppUser user = appUserAccessRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        String normalizedVehicleNumber = request.vehicleNumber().trim().toUpperCase();

        Vehicle vehicle = vehicleAccessRepository.findByVehicleNumberIgnoreCase(normalizedVehicleNumber)
                .map(existingVehicle -> validateAndReuseVehicle(existingVehicle, request, user))
                .orElseGet(() -> createVehicle(normalizedVehicleNumber, request, user));

        if (parkingSessionAccessRepository.existsByVehicleIdAndStatus(vehicle.getId(), SessionStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle already has an active parking session");
        }

        SlotType requiredSlotType = mapToSlotType(vehicle.getType().name());

        ParkingSlot slot = parkingSlotAccessRepository
                .findFirstByTypeAndStatusOrderByIdAsc(requiredSlotType, SlotStatus.AVAILABLE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No available slot for vehicle type"));

        slot.setStatus(SlotStatus.OCCUPIED);
        parkingSlotAccessRepository.save(slot);

        ParkingSession session = ParkingSession.builder()
                .vehicle(vehicle)
                .slot(slot)
                .user(user)
                .entryTime(LocalDateTime.now())
                .status(SessionStatus.ACTIVE)
                .build();

        ParkingSession savedSession = parkingSessionAccessRepository.save(session);

        return new ParkingEnterResponse(
                savedSession.getId(),
                vehicle.getVehicleNumber(),
                vehicle.getType(),
                slot.getId(),
                slot.getSlotNumber(),
                user.getId(),
                savedSession.getEntryTime(),
                savedSession.getStatus()
        );
    }

    @Transactional
    public ParkingExitResponse exit(ParkingExitRequest request) {
        validateExitRequest(request);

        String normalizedVehicleNumber = request.vehicleNumber().trim().toUpperCase();

        Vehicle vehicle = vehicleAccessRepository.findByVehicleNumberIgnoreCase(normalizedVehicleNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        ParkingSession session = parkingSessionAccessRepository
                .findFirstByVehicleIdAndStatusOrderByEntryTimeDesc(vehicle.getId(), SessionStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active parking session found for vehicle"));

        LocalDateTime exitTime = LocalDateTime.now();
        session.setExitTime(exitTime);
        session.setStatus(SessionStatus.COMPLETED);

        ParkingSlot slot = session.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        parkingSlotAccessRepository.save(slot);

        ParkingSession savedSession = parkingSessionAccessRepository.save(session);

        long durationInMinutes = Math.max(1, Duration.between(savedSession.getEntryTime(), exitTime).toMinutes());

        PricingPolicy pricingPolicy = pricingPolicyAccessRepository.findByVehicleType(vehicle.getType())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pricing policy not configured for vehicle type"));

        double amount = calculateAmount(durationInMinutes, pricingPolicy.getPricePerHour());

        Bill bill = Bill.builder()
                .session(savedSession)
                .amount(amount)
                .durationInMinutes(durationInMinutes)
                .calculatedAt(exitTime)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Bill savedBill = billAccessRepository.save(bill);

        return new ParkingExitResponse(
                savedSession.getId(),
                savedBill.getId(),
                vehicle.getVehicleNumber(),
                slot.getId(),
                slot.getSlotNumber(),
                savedSession.getEntryTime(),
                savedSession.getExitTime(),
                durationInMinutes,
                savedBill.getAmount(),
                savedSession.getStatus(),
                savedBill.getPaymentStatus()
        );
    }

    private Vehicle validateAndReuseVehicle(Vehicle existingVehicle, ParkingEnterRequest request, AppUser user) {
        if (!existingVehicle.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle belongs to a different user");
        }
        if (existingVehicle.getType() != request.vehicleType()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle type does not match existing vehicle record");
        }
        return existingVehicle;
    }

    private Vehicle createVehicle(String vehicleNumber, ParkingEnterRequest request, AppUser user) {
        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(vehicleNumber)
                .type(request.vehicleType())
                .owner(user)
                .build();

        return vehicleAccessRepository.save(vehicle);
    }

    private void validateEnterRequest(ParkingEnterRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (request.vehicleNumber() == null || request.vehicleNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vehicleNumber is required");
        }
        if (request.vehicleType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vehicleType is required");
        }
        if (request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
    }

    private void validateExitRequest(ParkingExitRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (request.vehicleNumber() == null || request.vehicleNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vehicleNumber is required");
        }
    }

    private SlotType mapToSlotType(String vehicleTypeName) {
        try {
            return SlotType.valueOf(vehicleTypeName);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle type is not supported by slot allocation");
        }
    }

    private double calculateAmount(long durationInMinutes, double pricePerHour) {
        double amount = (durationInMinutes / 60.0d) * pricePerHour;
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
