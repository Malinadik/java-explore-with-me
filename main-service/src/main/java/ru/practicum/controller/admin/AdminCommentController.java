package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentAdminDto;
import ru.practicum.service.CommentService;

@RestController
@RequestMapping(path = "/admin/comment")
@RequiredArgsConstructor
@Slf4j
public class AdminCommentController {
    private final CommentService service;

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentAdminDto getCommentById(@PathVariable Long commentId) {
        log.info("Получение коммента" + commentId + " Админом");
        return service.getAdminCommentById(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Удаление коммента" + commentId + " Админом");
        service.deleteCommentByAdmin(commentId);
    }
}
