package ru.practicum.mapper;

import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventEntryDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.model.Event;
import ru.practicum.model.Location;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.UserMapper.toShortDto;

public class EventMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event fromEntry(EventEntryDto entryDto) {
        return Event.builder().title(entryDto.getTitle()).annotation(entryDto.getAnnotation())
                .description(entryDto.getDescription())
                .participantLimit(entryDto.getParticipantLimit() != null ? entryDto.getParticipantLimit() : 0L)
                .eventDate(entryDto.getEventDate())
                .lat(entryDto.getLocation().getLat())
                .lon(entryDto.getLocation().getLon())
                .paid(entryDto.getPaid() != null ? entryDto.getPaid() : false)
                .requestModeration(entryDto.getRequestModeration() != null ? entryDto.getRequestModeration() : true)
                .build();
    }

    public static EventDto toDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .category(event.getCategory())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .initiator(toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate().format(formatter))
                .location(new Location(event.getLat(), event.getLon()))
                .createdOn(event.getCreatedOn().format(formatter))
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(formatter) : null)
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .requestModeration(event.getRequestModeration()).state(event.getState()).build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .category(event.getCategory())
                .description(event.getDescription())
                .initiator(toShortDto(event.getInitiator()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(formatter))
                .views(event.getViews())
                .paid(event.getPaid())
                .build();
    }

    public static List<EventShortDto> toEventShortDto(List<Event> events) {
        return events != null ? events.stream()
                .map(EventMapper::toEventShortDto).collect(Collectors.toList()) : new ArrayList<>();
    }
}
