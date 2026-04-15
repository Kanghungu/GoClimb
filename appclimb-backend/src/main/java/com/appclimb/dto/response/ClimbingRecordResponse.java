package com.appclimb.dto.response;

import com.appclimb.domain.ClimbingRecord;
import com.appclimb.domain.RecordEntry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ClimbingRecordResponse {
    private Long id;
    private Long gymId;
    private String gymName;
    private LocalDate recordDate;
    private List<EntryResponse> entries;

    @Getter
    @Builder
    public static class EntryResponse {
        private Long id;
        private Long colorId;
        private String colorName;
        private String colorHex;
        private int plannedCount;
        private int completedCount;

        public static EntryResponse from(RecordEntry e) {
            return EntryResponse.builder()
                    .id(e.getId())
                    .colorId(e.getColor().getId())
                    .colorName(e.getColor().getColorName())
                    .colorHex(e.getColor().getColorHex())
                    .plannedCount(e.getPlannedCount())
                    .completedCount(e.getCompletedCount())
                    .build();
        }
    }

    public static ClimbingRecordResponse from(ClimbingRecord record) {
        return ClimbingRecordResponse.builder()
                .id(record.getId())
                .gymId(record.getGym().getId())
                .gymName(record.getGym().getName())
                .recordDate(record.getRecordDate())
                .entries(record.getEntries().stream().map(EntryResponse::from).toList())
                .build();
    }
}
