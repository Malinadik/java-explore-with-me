package ru.practicum.server.service;

import ru.practicum.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.server.repository.StatsRepository;
import ru.practicum.server.exeption.NotSupportedException;
import ru.practicum.server.model.Hit;
import ru.practicum.dto.OutStats;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.server.model.HitMapper.fromDto;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository repository;

    public void saveHit(HitDto hitDto) {
        Hit hit = fromDto(hitDto);
        repository.save(hit);
    }

    public List<OutStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new NotSupportedException("Date should be valid!");
        }
        if (uris == null || uris.isEmpty()) {
            return repository.findAllWithoutUris(start, end);
        }
        if (unique) {
            return repository.findAllUnique(start, end, uris);
        }
        return repository.findAllNotUnique(start, end, uris);
    }

}
