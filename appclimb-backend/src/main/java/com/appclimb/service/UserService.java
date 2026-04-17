package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.domain.GymManager;
import com.appclimb.domain.User;
import com.appclimb.domain.UserFcmToken;
import com.appclimb.dto.request.FcmTokenRequest;
import com.appclimb.dto.response.GymResponse;
import com.appclimb.dto.response.MyGymResponse;
import com.appclimb.repository.GymManagerRepository;
import com.appclimb.repository.GymRepository;
import com.appclimb.repository.UserFcmTokenRepository;
import com.appclimb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GymManagerRepository gymManagerRepository;
    private final GymRepository gymRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;

    @Transactional(readOnly = true)
    public MyGymResponse getMyGym(Long userId) {
        return gymManagerRepository.findByUserId(userId)
                .map(MyGymResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("No assigned gym found."));
    }

    @Transactional
    public void assignManager(Long gymId, Long userId) {
        if (gymManagerRepository.existsByUserIdAndGymId(userId, gymId)) {
            throw new IllegalArgumentException("Manager is already assigned.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found."));

        gymManagerRepository.save(GymManager.builder()
                .user(user).gym(gym).build());
    }

    @Transactional
    public void removeManager(Long gymId, Long userId) {
        gymManagerRepository.deleteByUserIdAndGymId(userId, gymId);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getGymManagerUsers(Long gymId) {
        return gymManagerRepository.findByGymId(gymId).stream()
                .map(GymManager::getUser)
                .toList();
    }

    @Transactional
    public void registerOrUpdateFcmToken(Long userId, FcmTokenRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        var existing = userFcmTokenRepository.findByToken(request.getToken());
        if (existing.isPresent()) {
            // 이미 같은 토큰이 등록되어 있으면 update (다른 사용자일 경우 교체)
            userFcmTokenRepository.delete(existing.get());
        }

        UserFcmToken token = UserFcmToken.builder()
                .user(user)
                .token(request.getToken())
                .deviceType(request.getDeviceType())
                .build();

        userFcmTokenRepository.save(token);
    }

    @Transactional
    public void deleteFcmToken(String token) {
        userFcmTokenRepository.deleteByToken(token);
    }
}
