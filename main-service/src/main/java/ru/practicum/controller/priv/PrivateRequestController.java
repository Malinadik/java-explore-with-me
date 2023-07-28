package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @Valid @RequestParam(required = true) Long eventId) {
        log.info("Создание заявки на участие в событии ${eventId} пользователем " + userId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getOwnRequests(@PathVariable(name = "userId") Long userId) {
        log.info("Запрос собственных на участие в событии пользователем " + userId);
        return requestService.getOwnRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequests(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отмена заявки на участие в событии пользователем " + userId);
        return requestService.cancelOwnRequest(userId, requestId);
    }
}
