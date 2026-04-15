package com.appclimb.repository;

import com.appclimb.domain.DifficultyColor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DifficultyColorRepository extends JpaRepository<DifficultyColor, Long> {
    List<DifficultyColor> findByGymIdOrderByLevelOrder(Long gymId);
}
