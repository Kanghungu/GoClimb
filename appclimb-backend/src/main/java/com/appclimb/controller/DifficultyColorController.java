package com.appclimb.controller;

import com.appclimb.dto.request.DifficultyColorRequest;
import com.appclimb.dto.response.DifficultyColorResponse;
import com.appclimb.service.DifficultyColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/colors")
@RequiredArgsConstructor
public class DifficultyColorController {

    private final DifficultyColorService colorService;

    @GetMapping
    public ResponseEntity<List<DifficultyColorResponse>> getColors(@PathVariable Long gymId) {
        return ResponseEntity.ok(colorService.getColors(gymId));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<DifficultyColorResponse> createColor(@PathVariable Long gymId,
                                                                @Valid @RequestBody DifficultyColorRequest request) {
        return ResponseEntity.ok(colorService.createColor(gymId, request));
    }

    @PutMapping("/{colorId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<DifficultyColorResponse> updateColor(@PathVariable Long gymId,
                                                                @PathVariable Long colorId,
                                                                @Valid @RequestBody DifficultyColorRequest request) {
        return ResponseEntity.ok(colorService.updateColor(colorId, request));
    }

    @DeleteMapping("/{colorId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteColor(@PathVariable Long gymId,
                                             @PathVariable Long colorId) {
        colorService.deleteColor(colorId);
        return ResponseEntity.noContent().build();
    }
}
