package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.domain.GymManager;
import com.appclimb.domain.User;
import com.appclimb.dto.response.GymResponse;
import com.appclimb.dto.response.MyGymResponse;
import com.appclimb.repository.GymManagerRepository;
import com.appclimb.repository.GymRepository;
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

    // 내가 관리하는 지점 조회 (MANAGER 전용)
    @Transactional(readOnly = true)
    public MyGymResponse getMyGym(Long userId) {
        return gymManagerRepository.findByUserId(userId)
                .map(MyGymResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("배정된 지점이 없습니다."));
    }

    // 지점에 매니저 배정 (ADMIN 전용)
    @Transactional
    public void assignManager(Long gymId, Long userId) {
        if (gymManagerRepository.existsByUserIdAndGymId(userId, gymId)) {
            throw new IllegalArgumentException("이미 배정된 매니저입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("지점을 찾을 수 없습니다."));

        gymManagerRepository.save(GymManager.builder()
                .user(user).gym(gym).build());
    }

    // 지점 매니저 해제 (ADMIN 전용)
    @Transactional
    public void removeManager(Long gymId, Long userId) {
        gymManagerRepository.deleteByUserIdAndGymId(userId, gymId);
    }

    // 전체 사용자 목록 (ADMIN 전용 - 매니저 배정용)
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 지점 매니저 유저 목록 (ADMIN 전용)
    @Transactional(readOnly = true)
    public List<User> getGymManagerUsers(Long gymId) {
        return gymManagerRepository.findByGymId(gymId).stream()
                .map(GymManager::getUser)
                .toList();
    }
}
