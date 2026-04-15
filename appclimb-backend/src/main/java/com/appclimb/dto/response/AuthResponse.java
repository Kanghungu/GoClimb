package com.appclimb.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String nickname;
    private String role;
}
