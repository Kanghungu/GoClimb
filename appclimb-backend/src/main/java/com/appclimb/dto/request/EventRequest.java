package com.appclimb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EventRequest {

    @NotBlank(message = "이벤트 제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "시작 날짜는 필수입니다.")
    private LocalDate startDate;

    private LocalDate endDate;
}
