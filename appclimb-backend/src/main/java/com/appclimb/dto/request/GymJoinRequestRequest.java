package com.appclimb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GymJoinRequestRequest {

    @NotBlank(message = "지점 이름은 필수입니다.")
    private String gymName;

    private String gymAddress;

    private String gymDescription;
}
