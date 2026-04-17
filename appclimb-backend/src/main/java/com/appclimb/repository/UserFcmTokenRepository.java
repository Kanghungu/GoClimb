package com.appclimb.repository;

import com.appclimb.domain.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {
    List<UserFcmToken> findByUserId(Long userId);

    Optional<UserFcmToken> findByToken(String token);

    void deleteByToken(String token);
}
