package com.appclimb.controller;

import com.appclimb.dto.request.SettingScheduleRequest;
import com.appclimb.dto.response.SettingScheduleResponse;
import com.appclimb.service.SettingScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/schedules")
@RequiredArgsConstructor
public class SettingScheduleController {

    private final SettingScheduleService scheduleService;

    // 세팅 일정 조회 (전체 or ?month=2025-06)
    @GetMapping
    public ResponseEntity<List<SettingScheduleResponse>> getSchedules(
            @PathVariable Long gymId,
            @RequestParam(required = false) String month) {
        return ResponseEntity.ok(scheduleService.getSchedules(gymId, month));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<SettingScheduleResponse> createSchedule(
            @PathVariable Long gymId,
            @Valid @RequestBody SettingScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(gymId, request));
    }

    @PutMapping("/{scheduleId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<SettingScheduleResponse> updateSchedule(
            @PathVariable Long gymId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody SettingScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(gymId, scheduleId, request));
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long gymId,
                                                @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}
