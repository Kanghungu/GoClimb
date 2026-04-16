package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.domain.GymManager;
import com.appclimb.domain.GymStaff;
import com.appclimb.dto.request.GymStaffRequest;
import com.appclimb.dto.response.GymStaffResponse;
import com.appclimb.repository.GymManagerRepository;
import com.appclimb.repository.GymRepository;
import com.appclimb.repository.GymStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymStaffService {

    private final GymStaffRepository gymStaffRepository;
    private final GymRepository gymRepository;
    private final GymManagerRepository gymManagerRepository;

    @Transactional(readOnly = true)
    public List<GymStaffResponse> getStaff(Long gymId) {
        return gymStaffRepository.findByGymIdOrderByStaffRoleAscNameAsc(gymId).stream()
                .map(GymStaffResponse::from)
                .toList();
    }

    @Transactional
    public GymStaffResponse addStaff(Long gymId, Long userId, GymStaffRequest request) {
        validateManager(gymId, userId);
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("지점을 찾을 수 없습니다."));

        GymStaff staff = GymStaff.builder()
                .gym(gym)
                .name(request.getName())
                .staffRole(request.getStaffRole())
                .note(request.getNote())
                .build();

        return GymStaffResponse.from(gymStaffRepository.save(staff));
    }

    @Transactional
    public GymStaffResponse updateStaff(Long gymId, Long staffId, Long userId, GymStaffRequest request) {
        validateManager(gymId, userId);
        GymStaff staff = gymStaffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

        if (!staff.getGym().getId().equals(gymId)) {
            throw new IllegalArgumentException("해당 지점의 직원이 아닙니다.");
        }

        staff.update(request.getName(), request.getStaffRole(), request.getNote());
        return GymStaffResponse.from(gymStaffRepository.save(staff));
    }

    @Transactional
    public void removeStaff(Long gymId, Long staffId, Long userId) {
        validateManager(gymId, userId);
        GymStaff staff = gymStaffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

        if (!staff.getGym().getId().equals(gymId)) {
            throw new IllegalArgumentException("해당 지점의 직원이 아닙니다.");
        }

        gymStaffRepository.delete(staff);
    }

    private void validateManager(Long gymId, Long userId) {
        GymManager manager = gymManagerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("배정된 지점이 없습니다."));
        if (!manager.getGym().getId().equals(gymId)) {
            throw new IllegalArgumentException("해당 지점의 관리자가 아닙니다.");
        }
    }
}
