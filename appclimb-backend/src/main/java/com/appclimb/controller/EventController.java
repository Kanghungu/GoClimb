package com.appclimb.controller;

import com.appclimb.dto.request.EventRequest;
import com.appclimb.dto.response.EventResponse;
import com.appclimb.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents(@PathVariable Long gymId) {
        return ResponseEntity.ok(eventService.getEvents(gymId));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@PathVariable Long gymId,
                                                      @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(gymId, request));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long gymId,
                                                      @PathVariable Long eventId,
                                                      @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, request));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long gymId,
                                             @PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
