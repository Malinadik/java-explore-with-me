package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.RequestStatusUpdate;
import ru.practicum.dto.RequestStatusUpdateResult;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.mapper.RequestMapper.toRequestDto;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;
    private final EventRepository events;
    private final UserRepository users;

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found!"));
        Event event = events.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found!"));
        checkRequest(userId, event);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            Request request = Request.builder().created(LocalDateTime.now())
                    .requester(user).event(event).status(RequestStatus.CONFIRMED).build();
            return toRequestDto(repository.save(request));
        }

        Request request = Request.builder().created(LocalDateTime.now())
                .requester(user).event(event).status(RequestStatus.PENDING).build();
        return toRequestDto(repository.save(request));
    }

    public RequestStatusUpdateResult confirmRequest(Long userId, Long eventId, RequestStatusUpdate statusUpdate) {
        checkUser(userId);
        Event event = events.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found!"));
        Long max = event.getParticipantLimit();
        Long current = repository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (max.longValue() == current.longValue()) {
            throw new ConflictException("Limit already reached!");
        }
        List<Request> requests = repository.findAllById(statusUpdate.getRequestIds());
        if (requests.stream().anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING))) {
            throw new ConflictException("Request already proceed!");
        }
        RequestStatusUpdateResult result = RequestStatusUpdateResult.builder().confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>()).build();
        if (statusUpdate.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (Request request : requests) {
                if (max.longValue() != current.longValue()) {
                    current++;
                    request.setStatus(RequestStatus.CONFIRMED);
                    result.getConfirmedRequests().add(toRequestDto(request));
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    result.getRejectedRequests().add(toRequestDto(request));
                }
            }
        } else if (statusUpdate.getStatus().equals(RequestStatus.REJECTED)) {
            for (Request request : requests) {
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(toRequestDto(request));

            }
        }
        if (!requests.isEmpty()) {
            repository.saveAll(requests);
        }
        System.out.println(result);
        return result;
    }

    public List<ParticipationRequestDto> getRequestOwnEvents(Long eventId, Long userId) {
        checkUser(userId);
        return repository.findByEventIdAndEventInitiatorId(eventId, userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getOwnRequests(Long userId) {
        checkUser(userId);
        return repository.findByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        Request event = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found!"));
        if (!Objects.equals(event.getRequester().getId(), userId)) {
            throw new NotFoundException("It's not your request!");
        }
        event.setStatus(RequestStatus.CANCELED);
        return toRequestDto(repository.save(event));
    }

    private void checkUser(Long userId) {
        if (!users.existsById(userId)) {
            throw new NotFoundException("User not found!");
        }
    }

    private void checkRequest(Long userId, Event event) {
        if (event.getParticipantLimit() != 0 && (userId.longValue() == event.getInitiator().getId().longValue()
                || !event.getState().equals(EventState.PUBLISHED)
                || repository.existsByEventIdAndRequesterId(event.getId(), userId)
                || repository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED) >= event.getParticipantLimit())) {
            System.out.println(repository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED));
            throw new ConflictException("Request isnt valid!");
        }
    }
}
