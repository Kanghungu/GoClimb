package com.appclimb.repository;

import com.appclimb.domain.GymStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GymStaffRepository extends JpaRepository<GymStaff, Long> {
    List<GymStaff> findByGymIdOrderByStaffRoleAscNameAsc(Long gymId);
}
