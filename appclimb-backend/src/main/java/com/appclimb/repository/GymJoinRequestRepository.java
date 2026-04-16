package com.appclimb.repository;

import com.appclimb.domain.GymJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GymJoinRequestRepository extends JpaRepository<GymJoinRequest, Long> {
    List<GymJoinRequest> findAllByOrderByCreatedAtDesc();
    List<GymJoinRequest> findByStatusOrderByCreatedAtDesc(GymJoinRequest.Status status);
    boolean existsByRequesterIdAndStatus(Long requesterId, GymJoinRequest.Status status);
}
