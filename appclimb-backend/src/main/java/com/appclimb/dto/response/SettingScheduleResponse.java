package com.appclimb.dto.response;

import com.appclimb.domain.SettingSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SettingScheduleResponse {
    private Long id;
    private Long gymId;
    private Long sectorId;
    private String sectorName;
    private LocalDate settingDate;
    private String description;
    private LocalDateTime createdAt;

    public static SettingScheduleResponse from(SettingSchedule s) {
        return SettingScheduleResponse.builder()
                .id(s.getId())
                .gymId(s.getGym().getId())
                .sectorId(s.getSector() != null ? s.getSector().getId() : null)
                .sectorName(s.getSector() != null ? s.getSector().getName() : null)
                .settingDate(s.getSettingDate())
                .description(s.getDescription())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
