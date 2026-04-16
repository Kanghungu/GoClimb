package com.appclimb.dto.response;

import com.appclimb.domain.GymJoinRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GymJoinRequestResponse {

    private Long id;
    private Long requesterId;
    private String requesterNickname;
    private String requesterEmail;
    private String gymName;
    private String gymAddress;
    private String gymDescription;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public static GymJoinRequestResponse from(GymJoinRequest req) {
        return GymJoinRequestResponse.builder()
                .id(req.getId())
                .requesterId(req.getRequester().getId())
                .requesterNickname(req.getRequester().getNickname())
                .requesterEmail(req.getRequester().getEmail())
                .gymName(req.getGymName())
                .gymAddress(req.getGymAddress())
                .gymDescription(req.getGymDescription())
                .status(req.getStatus().name())
                .createdAt(req.getCreatedAt())
                .reviewedAt(req.getReviewedAt())
                .build();
    }
}
