package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.domain.User;
import com.appclimb.domain.UserFavoriteGym;
import com.appclimb.dto.response.GymResponse;
import com.appclimb.repository.GymRepository;
import com.appclimb.repository.UserFavoriteGymRepository;
import com.appclimb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteGymRepository favoriteRepository;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;

    @Transactional(readOnly = true)
    public List<GymResponse> getMyFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(fav -> GymResponse.from(fav.getGym()))
                .toList();
    }

    @Transactional
    public void addFavorite(Long userId, Long gymId) {
        if (favoriteRepository.existsByUserIdAndGymId(userId, gymId)) {
            throw new IllegalArgumentException("이미 즐겨찾기한 지점입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));

        favoriteRepository.save(UserFavoriteGym.builder()
                .user(user).gym(gym).build());
    }

    @Transactional
    public void removeFavorite(Long userId, Long gymId) {
        if (!favoriteRepository.existsByUserIdAndGymId(userId, gymId)) {
            throw new IllegalArgumentException("즐겨찾기에 없는 지점입니다.");
        }
        favoriteRepository.deleteByUserIdAndGymId(userId, gymId);
    }
}
