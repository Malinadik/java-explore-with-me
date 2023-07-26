package ru.practicum.server.model;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class HitMapper {

    public HitDto toDto(Hit hit) {
        return HitDto.builder().app(hit.getApp())
                .uri(hit.getUri()).ip(hit.getIp()).timestamp(hit.getTimestamp().toString()).build();
    }

    public Hit fromDto(HitDto hitDto) {
        Hit hit = Hit.builder().app(hitDto.getApp())
                .uri(hitDto.getUri()).ip(hitDto.getIp()).build();
        LocalDateTime dateTime =
                LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(hitDto.getTimestamp()));
        hit.setTimestamp(dateTime);
        return hit;
    }
}
