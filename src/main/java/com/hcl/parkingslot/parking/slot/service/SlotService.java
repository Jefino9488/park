package com.hcl.parkingslot.parking.slot.service;

import com.hcl.parkingslot.parking.entity.ParkingLot;
import com.hcl.parkingslot.parking.entity.ParkingSlot;
import com.hcl.parkingslot.parking.enums.SlotStatus;
import com.hcl.parkingslot.parking.enums.SlotType;
import com.hcl.parkingslot.parking.slot.dto.CreateSlotRequest;
import com.hcl.parkingslot.parking.slot.dto.SlotResponse;
import com.hcl.parkingslot.parking.slot.repository.ParkingLotFeatureRepository;
import com.hcl.parkingslot.parking.slot.repository.ParkingSlotFeatureRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final ParkingSlotFeatureRepository parkingSlotFeatureRepository;
    private final ParkingLotFeatureRepository parkingLotFeatureRepository;

    @Transactional(readOnly = true)
    public List<SlotResponse> findSlots(SlotStatus status, SlotType type) {
        List<ParkingSlot> slots = parkingSlotFeatureRepository.findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        });

        return slots.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SlotResponse getSlotById(Long id) {
        ParkingSlot slot = parkingSlotFeatureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
        return toResponse(slot);
    }

    @Transactional
    public SlotResponse createSlot(CreateSlotRequest request) {
        validateCreateRequest(request);

        if (parkingSlotFeatureRepository.existsBySlotNumber(request.slotNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot number already exists");
        }

        ParkingLot parkingLot = parkingLotFeatureRepository.findById(request.parkingLotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parking lot not found"));

        SlotStatus slotStatus = request.status() == null ? SlotStatus.AVAILABLE : request.status();

        ParkingSlot slot = ParkingSlot.builder()
                .slotNumber(request.slotNumber().trim())
                .type(request.type())
                .status(slotStatus)
                .parkingLot(parkingLot)
                .build();

        ParkingSlot saved = parkingSlotFeatureRepository.save(slot);
        return toResponse(saved);
    }

    private void validateCreateRequest(CreateSlotRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (request.slotNumber() == null || request.slotNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slotNumber is required");
        }
        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (request.parkingLotId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parkingLotId is required");
        }
    }

    private SlotResponse toResponse(ParkingSlot slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getSlotNumber(),
                slot.getType(),
                slot.getStatus(),
                slot.getParkingLot().getId()
        );
    }
}
