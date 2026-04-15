package com.appclimb.controller;

import com.appclimb.dto.request.GymRequest;
import com.appclimb.dto.response.GymResponse;
import com.appclimb.service.GymService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;

    @GetMapping
    public ResponseEntity<List<GymResponse>> getAllGyms() {
        return ResponseEntity.ok(gymService.getAllGyms());
    }

    @GetMapping("/{gymId}")
    public ResponseEntity<GymResponse> getGym(@PathVariable Long gymId) {
        return ResponseEntity.ok(gymService.getGym(gymId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymResponse> createGym(@Valid @RequestBody GymRequest request) {
        return ResponseEntity.ok(gymService.createGym(request));
    }

    @PutMapping("/{gymId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<GymResponse> updateGym(@PathVariable Long gymId,
                                                  @Valid @RequestBody GymRequest request) {
        return ResponseEntity.ok(gymService.updateGym(gymId, request));
    }

    @DeleteMapping("/{gymId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGym(@PathVariable Long gymId) {
        gymService.deleteGym(gymId);
        return ResponseEntity.noContent().build();
    }
}
