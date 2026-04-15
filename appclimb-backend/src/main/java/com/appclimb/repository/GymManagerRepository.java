package com.appclimb.repository;

import com.appclimb.domain.GymManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GymManagerRepository extends JpaRepository<GymManager, Long> {
    Optional<GymManager> findByUserId(Long userId);
    List<GymManager> findByGymId(Long gymId);
    boolean existsByUserIdAndGymId(Long userId, Long gymId);
    void deleteByUserIdAndGymId(Long userId, Long gymId);
}
