package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentEntryDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users/{userId}/comment")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {
    private final CommentService service;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody CommentEntryDto comment) {
        log.info("Добавление коммента пользователем " + userId);
        return service.addComment(userId, eventId, comment);
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody CommentEntryDto comment) {
        log.info("обновление коммента пользователем " + userId);
        return service.updateComment(userId, commentId, comment);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByOwner(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("удаление коммента " + commentId + " пользователем " + userId);
        service.deleteCommentByOwner(userId, commentId);
    }

    @PatchMapping("/{commentId}/like")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto likeComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Добавление лайка на коммент" + commentId + " пользователем " + userId);
        return service.likeComment(userId, commentId);
    }

    @PatchMapping("/{commentId}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto dislikeComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Добавление дизлайка на коммент" + commentId + " пользователем " + userId);
        return service.dislikeComment(userId, commentId);
    }
}
