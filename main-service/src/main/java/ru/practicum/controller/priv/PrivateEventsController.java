package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventsController {
    private final EventService eventService;

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(@PathVariable Long userId, @Valid @RequestBody EventEntryDto entryDto) {
        log.info("Создание события пользователем " + userId);
        return eventService.addEvent(userId, entryDto);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0", required = false) Integer from, @RequestParam(defaultValue = "10", required = false) Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Запрос события пользователем " + userId);
        return eventService.getEventsByInitiator(userId, pageable);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventByUser(@PathVariable Long userId, @PathVariable Long eventId, @Valid @RequestBody EventUserUpdateDto updateEventUserDto) {
        log.info("Обовления события ${eventId} пользователем " + userId);
        return eventService.updateEventByOwner(userId, eventId, updateEventUserDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос события ${eventId} пользователем " + userId);
        return eventService.getEventByInitiatorAndId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerOfEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос заявок на участие в событии ${eventId} пользователем " + userId);
        return requestService.getRequestOwnEvents(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResult updateRequests(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody RequestStatusUpdate requestStatusUpdateDto) {
        log.info("Обработка заявок на участие в событии ${eventId} пользователем " + userId);
        return requestService.confirmRequest(userId, eventId, requestStatusUpdateDto);
    }
}
