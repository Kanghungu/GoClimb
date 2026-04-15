package com.appclimb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DifficultyColorRequest {

    @NotBlank(message = "색깔 이름은 필수입니다.")
    private String colorName;

    @NotBlank(message = "HEX 코드는 필수입니다.")
    private String colorHex;

    @NotNull(message = "난이도 순서는 필수입니다.")
    private Integer levelOrder;
}
