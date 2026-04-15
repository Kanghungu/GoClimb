package com.appclimb.controller;

import com.appclimb.dto.response.GymResponse;
import com.appclimb.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<GymResponse>> getMyFavorites(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(favoriteService.getMyFavorites(userId));
    }

    @PostMapping("/{gymId}")
    public ResponseEntity<Void> addFavorite(@AuthenticationPrincipal Long userId,
                                             @PathVariable Long gymId) {
        favoriteService.addFavorite(userId, gymId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{gymId}")
    public ResponseEntity<Void> removeFavorite(@AuthenticationPrincipal Long userId,
                                                @PathVariable Long gymId) {
        favoriteService.removeFavorite(userId, gymId);
        return ResponseEntity.noContent().build();
    }
}
