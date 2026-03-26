package com.hcl.parkingslot.parking.access.repository;

import com.hcl.parkingslot.parking.entity.ParkingSession;
import com.hcl.parkingslot.parking.enums.SessionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSessionAccessRepository extends JpaRepository<ParkingSession, Long> {

    boolean existsByVehicleIdAndStatus(Long vehicleId, SessionStatus status);

    Optional<ParkingSession> findFirstByVehicleIdAndStatusOrderByEntryTimeDesc(Long vehicleId, SessionStatus status);
}
