package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.SortValue;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class PublicCommentController {
    private final CommentService service;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByEvent(@PathVariable Long eventId,
                                               @RequestParam(defaultValue = "DATE") SortValue sortValue,
                                               @RequestParam(defaultValue = "false") Boolean asc,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Получение комментов по событию " + eventId);
        return service.getCommentsByEvent(eventId, sortValue, asc, from, size);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByEventInitiator(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "DATE") SortValue sortValue,
                                                        @RequestParam(defaultValue = "false") Boolean asc,
                                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Получение комментов инициатору события " + userId);
        return service.getCommentsByEventInitiator(userId, sortValue, asc, from, size);
    }

    @GetMapping("/{userId}/user")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByUser(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "DATE") SortValue sortValue,
                                              @RequestParam(defaultValue = "false") Boolean asc,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Получение комментов по отправителю " + userId);
        return service.getCommentsByUser(userId, sortValue, asc, from, size);
    }

    @GetMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("получение коммента " + commentId);
        return service.getCommentById(commentId);
    }
}
