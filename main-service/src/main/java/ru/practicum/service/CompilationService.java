package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationEntryDto;
import ru.practicum.dto.CompilationUpdDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CompilationMapper.toCompDto;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public CompilationDto addCompilation(CompilationEntryDto entryDto) {
        checkTitle(entryDto.getTitle());
        Compilation compilation = Compilation.builder().events(new ArrayList<>())
                .title(entryDto.getTitle()).pinned(entryDto.getPinned() != null ? entryDto.getPinned() : false).build();
        if (entryDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(entryDto.getEvents())
                    .stream().map(this::setViewsAndRequests).collect(Collectors.toList());
            compilation.setEvents(events);
        }

        return toCompDto(repository.save(compilation));
    }

    public CompilationDto updateCompilation(Long compId, CompilationUpdDto updDto) {
        checkTitle(updDto.getTitle());
        Compilation compilation = repository.findById(compId).orElseThrow(() -> new NotFoundException("Compil not found!"));
        if (updDto.getTitle() != null) {
            compilation.setTitle(updDto.getTitle());
        }
        if (updDto.getPinned() != null) {
            compilation.setPinned(updDto.getPinned());
        }
        if (updDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updDto.getEvents())
                    .stream().map(this::setViewsAndRequests).collect(Collectors.toList());
            compilation.setEvents(events);
        }
        return toCompDto(repository.save(compilation));
    }

    public CompilationDto getCompilById(Long compId) {
        Compilation compilation = repository.findById(compId).orElseThrow(() -> new NotFoundException("Compil not found!"));
        return toCompDto(compilation);
    }

    public List<CompilationDto> getAllCompil(Boolean pinned, Pageable pageable) {
        if (pinned != null && pinned) {
            return repository.findAllByPinned(true, pageable).stream().map(CompilationMapper::toCompDto).collect(Collectors.toList());
        }
        return repository.findAll(pageable).stream().map(CompilationMapper::toCompDto).collect(Collectors.toList());
    }

    public void deleteCompilation(Long compId) {
        if (!repository.existsById(compId)) {
            throw new NotFoundException("Such comp not found!");
        }
        repository.deleteById(compId);
    }

    private Event setViewsAndRequests(Event event) {
        eventService.setViews(event);
        eventService.setConfRequests(event);
        return event;
    }

    private void checkTitle(String title) {
        if (repository.existsByTitle(title)) {
            throw new ConflictException("Compil with same title already exists!");
        }
    }
}
