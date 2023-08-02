package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.CompilationDto;
import ru.practicum.model.Compilation;

import static ru.practicum.mapper.EventMapper.toEventShortDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
