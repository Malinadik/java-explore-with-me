package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventUserUpdateDto;
import ru.practicum.model.EventState;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                @Valid @RequestBody EventUserUpdateDto updateEventAdminDto) {
        log.info("Обновление события " + eventId);
        return eventService.updateEventByAdmin(eventId, updateEventAdminDto);

    }

    @GetMapping
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                    @RequestParam(required = false) List<EventState> states,
                                    @RequestParam(required = false) List<Long> categoriesId,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Запрос списка событий от админа");
        return eventService.adminGetEventsByFilters(users, states, categoriesId, rangeStart, rangeEnd, from, size);
    }
}
