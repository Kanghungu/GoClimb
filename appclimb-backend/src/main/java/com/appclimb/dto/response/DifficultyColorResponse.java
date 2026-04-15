package com.appclimb.dto.response;

import com.appclimb.domain.DifficultyColor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyColorResponse {
    private Long id;
    private Long gymId;
    private String colorName;
    private String colorHex;
    private Integer levelOrder;

    public static DifficultyColorResponse from(DifficultyColor c) {
        return DifficultyColorResponse.builder()
                .id(c.getId())
                .gymId(c.getGym().getId())
                .colorName(c.getColorName())
                .colorHex(c.getColorHex())
                .levelOrder(c.getLevelOrder())
                .build();
    }
}
