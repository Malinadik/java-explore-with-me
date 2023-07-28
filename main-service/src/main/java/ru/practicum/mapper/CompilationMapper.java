package ru.practicum.mapper;

import ru.practicum.dto.CompilationDto;
import ru.practicum.model.Compilation;

import static ru.practicum.mapper.EventMapper.toEventShortDto;

public class CompilationMapper {
    public static CompilationDto toCompDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(toEventShortDto(compilation.getEvents()))
                .pinned(compilation.getPinned() != null ? compilation.getPinned() : null)
                .build();
    }
}
