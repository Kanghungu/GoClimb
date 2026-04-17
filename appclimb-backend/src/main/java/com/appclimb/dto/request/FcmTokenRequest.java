package com.appclimb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FcmTokenRequest {

    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private String token;

    @NotBlank(message = "기기 유형은 필수입니다.")
    private String deviceType;
}
