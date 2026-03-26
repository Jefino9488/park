package com.hcl.parkingslot.parking.access.repository;

import com.hcl.parkingslot.parking.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillAccessRepository extends JpaRepository<Bill, Long> {
}
