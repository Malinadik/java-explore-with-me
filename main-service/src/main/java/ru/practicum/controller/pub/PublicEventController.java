package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.Sort;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsWithParamsByUser(@RequestParam(required = false) SearchEventParams params) {
        log.info("Публичный запрос событий по фильтрам");
        return eventService.publicGetEventsByFilters(params.getText(), params.getCategories(), params.getPaid(), params.getOnlyAvailable(), params.getRangeStart(), params.getRangeEnd(), params.getSort(), params.getFrom(), params.getSize(), params.getRequest());
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("Публичный запрос конкретного события с айди: " + id);
        return eventService.getFullEventById(id, request);
    }
}
