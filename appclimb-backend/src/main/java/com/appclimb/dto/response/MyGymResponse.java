package com.appclimb.dto.response;

import com.appclimb.domain.GymManager;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyGymResponse {
    private Long gymId;
    private String gymName;
    private String gymAddress;

    public static MyGymResponse from(GymManager gm) {
        return MyGymResponse.builder()
                .gymId(gm.getGym().getId())
                .gymName(gm.getGym().getName())
                .gymAddress(gm.getGym().getAddress())
                .build();
    }
}
