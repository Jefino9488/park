package com.hcl.parkingslot.parking.slot.repository;

import com.hcl.parkingslot.parking.entity.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParkingSlotFeatureRepository extends JpaRepository<ParkingSlot, Long>, JpaSpecificationExecutor<ParkingSlot> {

    boolean existsBySlotNumber(String slotNumber);
}
