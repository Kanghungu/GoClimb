package com.appclimb.dto.response;

import com.appclimb.domain.GymStaff;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GymStaffResponse {

    private Long id;
    private Long gymId;
    private String name;
    private String staffRole;
    private String staffRoleLabel;
    private String note;
    private LocalDateTime createdAt;

    public static GymStaffResponse from(GymStaff staff) {
        return GymStaffResponse.builder()
                .id(staff.getId())
                .gymId(staff.getGym().getId())
                .name(staff.getName())
                .staffRole(staff.getStaffRole().name())
                .staffRoleLabel(toLabel(staff.getStaffRole()))
                .note(staff.getNote())
                .createdAt(staff.getCreatedAt())
                .build();
    }

    private static String toLabel(GymStaff.StaffRole role) {
        return switch (role) {
            case SETTER -> "세팅직원";
            case TEACHER -> "티칭직원";
            case FRONT -> "프론트직원";
            case MANAGER_STAFF -> "매니지먼트";
        };
    }
}
