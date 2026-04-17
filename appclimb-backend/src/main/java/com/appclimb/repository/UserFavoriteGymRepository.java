package com.appclimb.repository;

import com.appclimb.domain.UserFavoriteGym;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserFavoriteGymRepository extends JpaRepository<UserFavoriteGym, Long> {
    List<UserFavoriteGym> findByUserId(Long userId);

    List<UserFavoriteGym> findByGymId(Long gymId);

    Optional<UserFavoriteGym> findByUserIdAndGymId(Long userId, Long gymId);

    boolean existsByUserIdAndGymId(Long userId, Long gymId);

    void deleteByUserIdAndGymId(Long userId, Long gymId);
}
