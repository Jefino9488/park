package com.hcl.parkingslot.parking.access.repository;

import com.hcl.parkingslot.parking.entity.ParkingSlot;
import com.hcl.parkingslot.parking.enums.SlotStatus;
import com.hcl.parkingslot.parking.enums.SlotType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSlotAccessRepository extends JpaRepository<ParkingSlot, Long> {

    Optional<ParkingSlot> findFirstByTypeAndStatusOrderByIdAsc(SlotType type, SlotStatus status);
}
