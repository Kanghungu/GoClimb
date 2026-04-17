package com.appclimb.service;

import com.appclimb.repository.UserFcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    /**
     * 특정 사용자에게 푸시 알림 발송
     * Firebase Admin SDK 없이 stub으로 구현
     * 실제 Firebase 연결은 추후 google-services.json 발급 후 활성화 예정
     */
    public void sendToUser(Long userId, String title, String body) {
        log.info("[FCM STUB] sendToUser - userId: {}, title: {}, body: {}", userId, title, body);
        var tokens = userFcmTokenRepository.findByUserId(userId);
        for (var token : tokens) {
            log.info("[FCM STUB] Would send to device: token={}, deviceType={}", token.getToken(), token.getDeviceType());
        }
    }

    /**
     * 여러 사용자에게 푸시 알림 발송
     */
    public void sendToUsers(List<Long> userIds, String title, String body) {
        log.info("[FCM STUB] sendToUsers - userCount: {}, title: {}, body: {}", userIds.size(), title, body);
        for (Long userId : userIds) {
            sendToUser(userId, title, body);
        }
    }
}
