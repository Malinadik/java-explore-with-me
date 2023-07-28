package ru.practicum.service;



import ru.practicum.client.StatClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.mapper.EventMapper.fromEntry;
import static ru.practicum.mapper.EventMapper.toDto;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final RequestRepository requests;
    private final EntityManager manager;
    private final StatClient client;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public EventDto addEvent(Long userId, EventEntryDto entryDto) {
        Event event = fromEntry(entryDto);
        event.setCategory(categories.findById(entryDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Cat not Found!")));
        event.setInitiator(users.findById(userId).orElseThrow(() -> new NotFoundException("User not Found!")));
        event.setCreatedOn(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
        event.setState(EventState.PENDING);
        event.setViews(0L);
        event.setConfirmedRequests(0L);
        return toDto(repository.save(event));
    }

    public EventDto updateEventByOwner(Long userId, Long eventId, EventUserUpdateDto updateDto) {
        if (!users.existsById(userId)) {
            throw new NotFoundException("User not found!");
        }
        Event event = repository.findById(eventId).orElseThrow(() -> new NotFoundException("Cat not Found!"));
        if (event.getState().equals(EventState.PUBLISHED) || !Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Already published!");
        }
        update(event, updateDto);
        if (updateDto.getStateAction() != null) {
            switch (updateDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new ConflictException("U r not admin!");
            }
        }

        return toDto(repository.save(event));
    }

    public EventDto updateEventByAdmin(Long eventId, EventUserUpdateDto updateDto) {
        Event event = repository.findById(eventId).orElseThrow(() -> new NotFoundException("Cat not Found!"));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Already published!");
        }
        update(event, updateDto);
        System.out.println(updateDto.getStateAction());
        if (updateDto.getStateAction() != null) {
            switch (updateDto.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                default:
                    throw new ConflictException("U r not owner!");
            }
        }
        return toDto(repository.save(event));
    }

    public List<EventShortDto> getEventsByInitiator(Long userId, Pageable pageable) {
        if (!users.existsById(userId)) {
            throw new NotFoundException("User not found!");
        }
        return repository.findAllByInitiatorId(userId, pageable).stream().map(this::setViews).map(this::setConfRequests)
                .map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public EventDto getEventByInitiatorAndId(Long userId, Long eventId) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Event not found!");
        }
        setViews(event);
        setConfRequests(event);
        return toDto(event);
    }

    public EventDto getFullEventById(Long eventId, HttpServletRequest request) {
        Event event = repository.findByIdAndState(eventId, EventState.PUBLISHED);
        if (event == null) {
            throw new NotFoundException("Event not found!");
        }
        sendHit(request);
        setViews(event);
        setConfRequests(event);
        return toDto(event);
    }

    public List<EventShortDto> publicGetEventsByFilters(String text,
                                                        List<Long> categories,
                                                        Boolean paid,
                                                        Boolean onlyAvailable,
                                                        LocalDateTime start,
                                                        LocalDateTime end,
                                                        Sort sort,
                                                        Integer from, Integer size, HttpServletRequest request) {
        checkDate(start, end);
        List<Event> events;
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate predicate = builder.conjunction();
        Order order = builder.asc(root);
        predicate = builder.and(predicate, root.get("state").in(EventState.PUBLISHED));
        if (text != null && !text.isEmpty()) {
            Predicate annotation = builder.like(builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%");
            Predicate description = builder.like(builder.lower(root.get("description")), "%" + text.toLowerCase() + "%");
            predicate = builder.and(predicate, builder.or(annotation, description));
        }
        if (categories != null) {
            predicate = builder.and(predicate, root.get("category").get("id").in(categories));
        }
        if (paid != null) {
            predicate = builder.and(predicate, builder.equal(root.get("paid"), paid));
        }
        if (start != null) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));
        }
        if (end != null) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));
        }
        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    order = builder.asc(root.get("eventDate"));
                    break;
                case VIEWS:
                    order = builder.asc(root.get("views"));
                    break;
            }

        }
        events = manager.createQuery(query.select(root)
                        .where(predicate).orderBy(order))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::setConfRequests)
                .collect(Collectors.toList());
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        sendHits(request, ids);
        System.out.println(ids);
        if (onlyAvailable) {
            return events.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .map(this::setViews)
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }

        return events.stream()
                .map(this::setViews)
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public List<EventDto> adminGetEventsByFilters(List<Long> ids, List<EventState> states,
                                                  List<Long> categories, LocalDateTime start,
                                                  LocalDateTime end, Integer from, Integer size) {
        checkDate(start, end);
        System.out.println(start + " " + end);
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate predicate = builder.conjunction();
        Order order = builder.desc(root);
        if (ids != null && !ids.isEmpty()) {
            predicate = builder.and(predicate, root.get("initiator").get("id").in(ids));
        }
        if (states != null && !states.isEmpty()) {
            predicate = builder.and(predicate, root.get("state").in(states));
        }
        if (categories != null) {
            predicate = builder.and(predicate, root.get("category").get("id").in(categories));
        }
        if (start != null) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));
        }
        if (end != null) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));
        }

        return manager.createQuery(query.select(root)
                        .where(predicate).orderBy(order))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::setConfRequests)
                .map(this::setViews)
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    private void update(Event event, EventUserUpdateDto updateDto) {
        if (updateDto.getAnnotation() != null) {
            event.setAnnotation(updateDto.getAnnotation());
        }
        if (updateDto.getTitle() != null) {
            event.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            event.setDescription(updateDto.getDescription());
        }
        if (updateDto.getCategory() != null) {
            Category cat = categories.findById(updateDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Cat not found"));
            event.setCategory(cat);
        }
        if (updateDto.getLocation() != null) {
            event.setLat(updateDto.getLocation().getLat());
            event.setLon(updateDto.getLocation().getLon());
        }
        if (updateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateDto.getParticipantLimit());
        }

        if (updateDto.getEventDate() != null) {
            if (updateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new NotAvailableException("Date incorrect!");
            }
            event.setEventDate(updateDto.getEventDate());
        }
        if (updateDto.getRequestModeration() != null) {
            event.setRequestModeration(updateDto.getRequestModeration());
        }
        if (updateDto.getPaid() != null) {
            event.setPaid(updateDto.getPaid());
        }
    }

    protected Event setConfRequests(Event event) {
        event.setConfirmedRequests(requests.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED));
        return event;
    }

    private void sendHits(HttpServletRequest request, List<Long> ids) {
        for (Long eventId : ids) {
            client.saveHit(HitDto.builder()
                    .app("main-service")
                    .ip(request.getRemoteAddr())
                    .uri("/events/" + eventId)
                    .timestamp(LocalDateTime.now().format(formatter))
                    .build());
        }
        client.saveHit(HitDto.builder()
                .app("main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now().format(formatter))
                .build());
    }

    private void sendHit(HttpServletRequest request) {
        client.saveHit(HitDto.builder()
                .app("main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now().format(formatter))
                .build());
    }


    protected Event setViews(Event event) {
        try {
            List<String> uris = new ArrayList<>();
            uris.add("/events/" + event.getId());

            ObjectMapper mapper = new ObjectMapper();
            ResponseEntity<Object> stat = client.getStatistics("1900-01-01 00:00:00",
                    "2100-01-01 00:00:00", uris, false);
            System.out.println(stat.getBody());
            List<OutStats> statsDto = Arrays.asList(mapper.readValue(mapper.writeValueAsString(stat.getBody()),
                    OutStats[].class));
            if (statsDto.isEmpty()) {
                return event;
            }
            System.out.println(statsDto);
            Long views = statsDto.size() + event.getViews();
            event.setViews(views);
            return event;
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    private void checkDate(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new NotAvailableException("Date incorrect!");
        }
    }
}
