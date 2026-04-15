package com.appclimb.controller;

import com.appclimb.dto.request.SectorRequest;
import com.appclimb.dto.response.SectorResponse;
import com.appclimb.service.SectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/sectors")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @GetMapping
    public ResponseEntity<List<SectorResponse>> getSectors(@PathVariable Long gymId) {
        return ResponseEntity.ok(sectorService.getSectors(gymId));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<SectorResponse> createSector(@PathVariable Long gymId,
                                                        @Valid @RequestBody SectorRequest request) {
        return ResponseEntity.ok(sectorService.createSector(gymId, request));
    }

    @DeleteMapping("/{sectorId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSector(@PathVariable Long gymId,
                                              @PathVariable Long sectorId) {
        sectorService.deleteSector(sectorId);
        return ResponseEntity.noContent().build();
    }
}
