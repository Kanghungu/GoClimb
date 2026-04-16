package com.appclimb.controller;

import com.appclimb.dto.request.GymJoinRequestRequest;
import com.appclimb.dto.response.GymJoinRequestResponse;
import com.appclimb.service.GymJoinRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GymJoinRequestController {

    private final GymJoinRequestService gymJoinRequestService;

    /** 지점 가입 신청 (인증된 사용자) */
    @PostMapping("/api/gym-join-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GymJoinRequestResponse> submitRequest(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody GymJoinRequestRequest request) {
        return ResponseEntity.ok(gymJoinRequestService.submitRequest(userId, request));
    }

    /** 전체 신청 목록 조회 (ADMIN) */
    @GetMapping("/api/admin/gym-join-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GymJoinRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(gymJoinRequestService.getAllRequests());
    }

    /** 대기 중인 신청 목록만 조회 (ADMIN) */
    @GetMapping("/api/admin/gym-join-requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GymJoinRequestResponse>> getPendingRequests() {
        return ResponseEntity.ok(gymJoinRequestService.getPendingRequests());
    }

    /** 신청 승인 (ADMIN) */
    @PostMapping("/api/admin/gym-join-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymJoinRequestResponse> approve(@PathVariable Long requestId) {
        return ResponseEntity.ok(gymJoinRequestService.approve(requestId));
    }

    /** 신청 거절 (ADMIN) */
    @PostMapping("/api/admin/gym-join-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymJoinRequestResponse> reject(@PathVariable Long requestId) {
        return ResponseEntity.ok(gymJoinRequestService.reject(requestId));
    }
}
