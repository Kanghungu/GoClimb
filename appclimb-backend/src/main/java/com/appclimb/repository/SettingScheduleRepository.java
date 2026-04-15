package com.appclimb.repository;

import com.appclimb.domain.SettingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SettingScheduleRepository extends JpaRepository<SettingSchedule, Long> {
    List<SettingSchedule> findByGymIdAndSettingDateBetween(Long gymId, LocalDate start, LocalDate end);
    List<SettingSchedule> findByGymId(Long gymId);
}
