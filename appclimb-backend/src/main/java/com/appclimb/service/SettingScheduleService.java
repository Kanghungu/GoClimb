package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.domain.Sector;
import com.appclimb.domain.SettingSchedule;
import com.appclimb.dto.request.SettingScheduleRequest;
import com.appclimb.dto.response.SettingScheduleResponse;
import com.appclimb.repository.GymRepository;
import com.appclimb.repository.SectorRepository;
import com.appclimb.repository.SettingScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingScheduleService {

    private final SettingScheduleRepository scheduleRepository;
    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;

    @Transactional(readOnly = true)
    public List<SettingScheduleResponse> getSchedules(Long gymId, String month) {
        if (month != null) {
            YearMonth ym = YearMonth.parse(month);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            return scheduleRepository.findByGymIdAndSettingDateBetween(gymId, start, end)
                    .stream().map(SettingScheduleResponse::from).toList();
        }
        return scheduleRepository.findByGymId(gymId)
                .stream().map(SettingScheduleResponse::from).toList();
    }

    @Transactional
    public SettingScheduleResponse createSchedule(Long gymId, SettingScheduleRequest request) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));

        Sector sector = null;
        if (request.getSectorId() != null) {
            sector = sectorRepository.findById(request.getSectorId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 섹터입니다."));
        }

        SettingSchedule schedule = SettingSchedule.builder()
                .gym(gym)
                .sector(sector)
                .settingDate(request.getSettingDate())
                .description(request.getDescription())
                .build();

        return SettingScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public SettingScheduleResponse updateSchedule(Long gymId, Long scheduleId, SettingScheduleRequest request) {
        SettingSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세팅 일정입니다."));

        Sector sector = null;
        if (request.getSectorId() != null) {
            sector = sectorRepository.findById(request.getSectorId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 섹터입니다."));
        }

        SettingSchedule updated = SettingSchedule.builder()
                .id(schedule.getId())
                .gym(schedule.getGym())
                .sector(sector)
                .settingDate(request.getSettingDate())
                .description(request.getDescription())
                .build();

        return SettingScheduleResponse.from(scheduleRepository.save(updated));
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new IllegalArgumentException("존재하지 않는 세팅 일정입니다.");
        }
        scheduleRepository.deleteById(scheduleId);
    }
}
