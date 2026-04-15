package com.appclimb.service;

import com.appclimb.domain.Event;
import com.appclimb.domain.Gym;
import com.appclimb.dto.request.EventRequest;
import com.appclimb.dto.response.EventResponse;
import com.appclimb.repository.EventRepository;
import com.appclimb.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final GymRepository gymRepository;

    @Transactional(readOnly = true)
    public List<EventResponse> getEvents(Long gymId) {
        return eventRepository.findByGymIdOrderByStartDateDesc(gymId)
                .stream().map(EventResponse::from).toList();
    }

    @Transactional
    public EventResponse createEvent(Long gymId, EventRequest request) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));
        Event event = Event.builder()
                .gym(gym)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));
        Event updated = Event.builder()
                .id(event.getId())
                .gym(event.getGym())
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return EventResponse.from(eventRepository.save(updated));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("존재하지 않는 이벤트입니다.");
        }
        eventRepository.deleteById(eventId);
    }
}
