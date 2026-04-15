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

    @GetMapping("/api/me/gym")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<MyGymResponse> getMyGym(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getMyGym(userId));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers().stream().map(UserResponse::from).toList()
        );
    }

    @GetMapping("/api/admin/gyms/{gymId}/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getGymManagers(@PathVariable Long gymId) {
        return ResponseEntity.ok(
                userService.getGymManagerUsers(gymId).stream().map(UserResponse::from).toList()
        );
    }

    @PostMapping("/api/admin/gyms/{gymId}/managers/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignManager(@PathVariable Long gymId, @PathVariable Long userId) {
        userService.assignManager(gymId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/admin/gyms/{gymId}/managers/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeManager(@PathVariable Long gymId, @PathVariable Long userId) {
        userService.removeManager(gymId, userId);
        return ResponseEntity.noContent().build();
    }
}
