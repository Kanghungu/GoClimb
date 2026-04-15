package com.appclimb.repository;

import com.appclimb.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    List<Sector> findByGymId(Long gymId);
}
