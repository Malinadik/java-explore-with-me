package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned, @RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Публичный запрос подборок событий");
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationService.getAllCompil(pinned, pageable);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Публичный запрос конкретной подборки " + compId);
        return compilationService.getCompilById(compId);
    }
}
