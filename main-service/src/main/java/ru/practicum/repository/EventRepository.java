package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Event findByIdAndState(Long eventId, EventState state);

    Event findByIdAndInitiatorId(Long eventId, Long userId);

    Boolean existsByCategoryId(Long catId);
}
