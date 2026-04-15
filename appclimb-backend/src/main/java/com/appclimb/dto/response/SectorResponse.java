package com.appclimb.dto.response;

import com.appclimb.domain.Sector;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectorResponse {
    private Long id;
    private Long gymId;
    private String name;
    private String description;

    public static SectorResponse from(Sector s) {
        return SectorResponse.builder()
                .id(s.getId())
                .gymId(s.getGym().getId())
                .name(s.getName())
                .description(s.getDescription())
                .build();
    }
}
