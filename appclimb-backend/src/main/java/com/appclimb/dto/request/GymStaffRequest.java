package com.appclimb.dto.request;

import com.appclimb.domain.GymStaff;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GymStaffRequest {

    @NotBlank(message = "직원 이름은 필수입니다.")
    private String name;

    @NotNull(message = "직원 역할은 필수입니다.")
    private GymStaff.StaffRole staffRole;

    private String note;
}
