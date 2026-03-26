package com.hcl.parkingslot.parking.slot.repository;

import com.hcl.parkingslot.parking.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotFeatureRepository extends JpaRepository<ParkingLot, Long> {
}
