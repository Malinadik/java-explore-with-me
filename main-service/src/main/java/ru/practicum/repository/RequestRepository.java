package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByEventIdAndEventInitiatorId(Long eventId, Long userId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByRequesterId(Long eventId);
}
