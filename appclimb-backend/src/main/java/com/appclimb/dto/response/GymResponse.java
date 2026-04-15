package com.appclimb.dto.response;

import com.appclimb.domain.Gym;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GymResponse {
    private Long id;
    private String name;
    private String address;
    private String description;
    private LocalDateTime createdAt;

    public static GymResponse from(Gym gym) {
        return GymResponse.builder()
                .id(gym.getId())
                .name(gym.getName())
                .address(gym.getAddress())
                .description(gym.getDescription())
                .createdAt(gym.getCreatedAt())
                .build();
    }
}
