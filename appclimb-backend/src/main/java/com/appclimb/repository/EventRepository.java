package com.appclimb.repository;

import com.appclimb.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByGymIdOrderByStartDateDesc(Long gymId);
}
