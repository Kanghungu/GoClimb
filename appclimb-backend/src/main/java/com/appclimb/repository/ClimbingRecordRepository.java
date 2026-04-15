package com.appclimb.repository;

import com.appclimb.domain.ClimbingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClimbingRecordRepository extends JpaRepository<ClimbingRecord, Long> {
    List<ClimbingRecord> findByUserIdAndRecordDateBetween(Long userId, LocalDate start, LocalDate end);
    Optional<ClimbingRecord> findByUserIdAndGymIdAndRecordDate(Long userId, Long gymId, LocalDate recordDate);
}
