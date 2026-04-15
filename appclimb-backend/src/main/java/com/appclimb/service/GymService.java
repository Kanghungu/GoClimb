package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.dto.request.GymRequest;
import com.appclimb.dto.response.GymResponse;
import com.appclimb.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;

    @Transactional(readOnly = true)
    public List<GymResponse> getAllGyms() {
        return gymRepository.findAll().stream()
                .map(GymResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public GymResponse getGym(Long gymId) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));
        return GymResponse.from(gym);
    }

    @Transactional
    public GymResponse createGym(GymRequest request) {
        Gym gym = Gym.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .build();
        return GymResponse.from(gymRepository.save(gym));
    }

    @Transactional
    public GymResponse updateGym(Long gymId, GymRequest request) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));
        Gym updated = Gym.builder()
                .id(gym.getId())
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .createdAt(gym.getCreatedAt())
                .build();
        return GymResponse.from(gymRepository.save(updated));
    }

    @Transactional
    public void deleteGym(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new IllegalArgumentException("존재하지 않는 지점입니다.");
        }
        gymRepository.deleteById(gymId);
    }
}
