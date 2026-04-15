package com.appclimb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SectorRequest {
    @NotBlank(message = "섹터명은 필수입니다.")
    private String name;
    private String description;
}
