package com.hcl.parkingslot.parking.access.repository;

import com.hcl.parkingslot.parking.entity.PricingPolicy;
import com.hcl.parkingslot.parking.enums.VehicleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingPolicyAccessRepository extends JpaRepository<PricingPolicy, Long> {

    Optional<PricingPolicy> findByVehicleType(VehicleType vehicleType);
}
