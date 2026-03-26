package com.hcl.parkingslot.parking.access.repository;

import com.hcl.parkingslot.parking.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserAccessRepository extends JpaRepository<AppUser, Long> {
}
