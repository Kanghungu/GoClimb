package com.appclimb.controller;

import com.appclimb.dto.request.GymStaffRequest;
import com.appclimb.dto.response.GymStaffResponse;
import com.appclimb.service.GymStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/staff")
@RequiredArgsConstructor
public class GymStaffController {

    private final GymStaffService gymStaffService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<GymStaffResponse>> getStaff(@PathVariable Long gymId) {
        return ResponseEntity.ok(gymStaffService.getStaff(gymId));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GymStaffResponse> addStaff(
            @PathVariable Long gymId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody GymStaffRequest request) {
        return ResponseEntity.ok(gymStaffService.addStaff(gymId, userId, request));
    }

    @PutMapping("/{staffId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GymStaffResponse> updateStaff(
            @PathVariable Long gymId,
            @PathVariable Long staffId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody GymStaffRequest request) {
        return ResponseEntity.ok(gymStaffService.updateStaff(gymId, staffId, userId, request));
    }

    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> removeStaff(
            @PathVariable Long gymId,
            @PathVariable Long staffId,
            @AuthenticationPrincipal Long userId) {
        gymStaffService.removeStaff(gymId, staffId, userId);
        return ResponseEntity.noContent().build();
    }
}
