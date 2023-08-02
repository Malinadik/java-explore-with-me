package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByCommentIdAndAuthorId(Long commentId, Long userId);

    Long countByCommentIdAndIslike(Long commentId, Boolean like);
}
