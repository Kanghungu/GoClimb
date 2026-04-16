package com.appclimb.service;

import com.appclimb.domain.Gym;
import com.appclimb.domain.GymJoinRequest;
import com.appclimb.domain.GymManager;
import com.appclimb.domain.User;
import com.appclimb.dto.request.GymJoinRequestRequest;
import com.appclimb.dto.response.GymJoinRequestResponse;
import com.appclimb.repository.GymJoinRequestRepository;
import com.appclimb.repository.GymManagerRepository;
import com.appclimb.repository.GymRepository;
import com.appclimb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymJoinRequestService {

    private final GymJoinRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GymManagerRepository gymManagerRepository;

    /**
     * 지점 가입 신청 (누구나 신청 가능)
     */
    @Transactional
    public GymJoinRequestResponse submitRequest(Long userId, GymJoinRequestRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (requestRepository.existsByRequesterIdAndStatus(userId, GymJoinRequest.Status.PENDING)) {
            throw new IllegalArgumentException("이미 대기 중인 신청이 있습니다.");
        }

        GymJoinRequest request = GymJoinRequest.builder()
                .requester(user)
                .gymName(dto.getGymName())
                .gymAddress(dto.getGymAddress())
                .gymDescription(dto.getGymDescription())
                .status(GymJoinRequest.Status.PENDING)
                .build();

        return GymJoinRequestResponse.from(requestRepository.save(request));
    }

    /**
     * 모든 신청 목록 조회 (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<GymJoinRequestResponse> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(GymJoinRequestResponse::from)
                .toList();
    }

    /**
     * PENDING 신청 목록만 조회 (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<GymJoinRequestResponse> getPendingRequests() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(GymJoinRequest.Status.PENDING).stream()
                .map(GymJoinRequestResponse::from)
                .toList();
    }

    /**
     * 신청 승인: 지점 생성 + 신청자 MANAGER 역할 부여 + GymManager 연결
     */
    @Transactional
    public GymJoinRequestResponse approve(Long requestId) {
        GymJoinRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        if (request.getStatus() != GymJoinRequest.Status.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신청입니다.");
        }

        // 지점 생성
        Gym gym = gymRepository.save(Gym.builder()
                .name(request.getGymName())
                .address(request.getGymAddress())
                .description(request.getGymDescription())
                .build());

        // 신청자 역할 MANAGER로 변경
        User requester = request.getRequester();
        requester.changeRole(User.Role.MANAGER);
        userRepository.save(requester);

        // GymManager 연결
        gymManagerRepository.save(GymManager.builder()
                .user(requester)
                .gym(gym)
                .build());

        request.approve();
        return GymJoinRequestResponse.from(requestRepository.save(request));
    }

    /**
     * 신청 거절
     */
    @Transactional
    public GymJoinRequestResponse reject(Long requestId) {
        GymJoinRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        if (request.getStatus() != GymJoinRequest.Status.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신청입니다.");
        }

        request.reject();
        return GymJoinRequestResponse.from(requestRepository.save(request));
    }
}
