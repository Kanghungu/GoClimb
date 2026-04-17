package com.appclimb.controller;

import com.appclimb.dto.request.ClimbingRecordRequest;
import com.appclimb.dto.response.ClimbingRecordResponse;
import com.appclimb.service.ClimbingRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class ClimbingRecordController {

    private final ClimbingRecordService recordService;

    // 내 운동 기록 조회 (?month=2025-06)
    @GetMapping
    public ResponseEntity<List<ClimbingRecordResponse>> getMyRecords(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String month) {
        return ResponseEntity.ok(recordService.getRecords(userId, month));
    }

    @PostMapping
    public ResponseEntity<ClimbingRecordResponse> createRecord(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ClimbingRecordRequest request) {
        return ResponseEntity.ok(recordService.createRecord(userId, request));
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<ClimbingRecordResponse> updateRecord(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long recordId,
            @Valid @RequestBody ClimbingRecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(userId, recordId, request));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long recordId) {
        recordService.deleteRecord(userId, recordId);
        return ResponseEntity.noContent().build();
    }
}
