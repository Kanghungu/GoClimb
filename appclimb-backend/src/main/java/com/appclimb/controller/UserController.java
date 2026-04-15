package com.appclimb.controller;

import com.appclimb.dto.response.MyGymResponse;
import com.appclimb.dto.response.UserResponse;
import com.appclimb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // MANAGER: 내 지점 조회
    @GetMapping("/api/me/gym")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<MyGymResponse> getMyGym(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getMyGym(userId));
    }

    // ADMIN: 전체 유저 목록
    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers().stream().map(UserResponse::from).toList()
        );
    }

    // ADMIN: 지점 매니저 목록
    @GetMapping("/api/admin/gyms/{gymId}/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getGymManagers(@PathVariable Long gymId) {
        return ResponseEntity.ok(userService.getGymManagerUsers(gymId));
    }

    // ADMIN: 지점에 매니저 배정
    @PostMapping("/api/admin/gyms/{gymId}/managers/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignManager(@PathVariable Long gymId, @PathVariable Long userId) {
        userService.assignManager(gymId, userId);
        return ResponseEntity.ok().build();
    }

    // ADMIN: 매니저 해제
    @DeleteMapping("/api/admin/gyms/{gymId}/managers/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeManager(@PathVariable Long gymId, @PathVariable Long userId) {
        userService.removeManager(gymId, userId);
        return ResponseEntity.noContent().build();
    }
}
