package com.appclimb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ClimbingRecordRequest {

    @NotNull(message = "지점 ID는 필수입니다.")
    private Long gymId;

    @NotNull(message = "운동 날짜는 필수입니다.")
    private LocalDate recordDate;

    private List<EntryRequest> entries;

    @Getter
    public static class EntryRequest {
        private Long colorId;
        private int plannedCount;
        private int completedCount;
    }
}
