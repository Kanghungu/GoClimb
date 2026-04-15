package com.appclimb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SettingScheduleRequest {

    private Long sectorId;

    @NotNull(message = "세팅 날짜는 필수입니다.")
    private LocalDate settingDate;

    private String description;
}
