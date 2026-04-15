package com.appclimb.service;

import com.appclimb.domain.DifficultyColor;
import com.appclimb.domain.Gym;
import com.appclimb.dto.request.DifficultyColorRequest;
import com.appclimb.dto.response.DifficultyColorResponse;
import com.appclimb.repository.DifficultyColorRepository;
import com.appclimb.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DifficultyColorService {

    private final DifficultyColorRepository colorRepository;
    private final GymRepository gymRepository;

    @Transactional(readOnly = true)
    public List<DifficultyColorResponse> getColors(Long gymId) {
        return colorRepository.findByGymIdOrderByLevelOrder(gymId)
                .stream().map(DifficultyColorResponse::from).toList();
    }

    @Transactional
    public DifficultyColorResponse createColor(Long gymId, DifficultyColorRequest request) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));
        DifficultyColor color = DifficultyColor.builder()
                .gym(gym)
                .colorName(request.getColorName())
                .colorHex(request.getColorHex())
                .levelOrder(request.getLevelOrder())
                .build();
        return DifficultyColorResponse.from(colorRepository.save(color));
    }

    @Transactional
    public DifficultyColorResponse updateColor(Long colorId, DifficultyColorRequest request) {
        DifficultyColor color = colorRepository.findById(colorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 난이도입니다."));
        DifficultyColor updated = DifficultyColor.builder()
                .id(color.getId())
                .gym(color.getGym())
                .colorName(request.getColorName())
                .colorHex(request.getColorHex())
                .levelOrder(request.getLevelOrder())
                .build();
        return DifficultyColorResponse.from(colorRepository.save(updated));
    }

    @Transactional
    public void deleteColor(Long colorId) {
        if (!colorRepository.existsById(colorId)) {
            throw new IllegalArgumentException("존재하지 않는 난이도입니다.");
        }
        colorRepository.deleteById(colorId);
    }
}
