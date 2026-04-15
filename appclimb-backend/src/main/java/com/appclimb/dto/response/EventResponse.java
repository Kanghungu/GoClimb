package com.appclimb.dto.response;

import com.appclimb.domain.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EventResponse {
    private Long id;
    private Long gymId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .gymId(event.getGym().getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();
    }
}
