package com.appclimb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GymRequest {

    @NotBlank(message = "지점명은 필수입니다.")
    private String name;

    private String address;
    private String description;
}
