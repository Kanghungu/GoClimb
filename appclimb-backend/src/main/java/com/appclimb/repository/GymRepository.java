package com.appclimb.repository;

import com.appclimb.domain.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GymRepository extends JpaRepository<Gym, Long> {
    List<Gym> findByNameContainingIgnoreCase(String name);
}
