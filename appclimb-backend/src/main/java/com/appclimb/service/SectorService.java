package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.domain.Sector;
import com.appclimb.dto.request.SectorRequest;
import com.appclimb.dto.response.SectorResponse;
import com.appclimb.repository.GymRepository;
import com.appclimb.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;
    private final GymRepository gymRepository;

    @Transactional(readOnly = true)
    public List<SectorResponse> getSectors(Long gymId) {
        return sectorRepository.findByGymId(gymId)
                .stream().map(SectorResponse::from).toList();
    }

    @Transactional
    public SectorResponse createSector(Long gymId, SectorRequest request) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));
        Sector sector = Sector.builder()
                .gym(gym)
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return SectorResponse.from(sectorRepository.save(sector));
    }

    @Transactional
    public void deleteSector(Long sectorId) {
        if (!sectorRepository.existsById(sectorId)) {
            throw new IllegalArgumentException("존재하지 않는 섹터입니다.");
        }
        sectorRepository.deleteById(sectorId);
    }
}
