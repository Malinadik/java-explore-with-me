package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CommentAdminDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentEntryDto;
import ru.practicum.dto.SortValue;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.CommentLike;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentLikeRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CommentMapper.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository likeRepository;

    public CommentDto addComment(Long userId, Long eventId, CommentEntryDto entryDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found!"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event Not Found!"));
        Comment comment = fromEntryComment(entryDto);
        comment.setCreated(LocalDateTime.now());
        comment.setEvent(event);
        comment.setAuthor(user);
        return toCommentDto(commentRepository.save(comment));
    }

    public CommentDto updateComment(Long userId, Long commentId, CommentEntryDto entryDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Not Found!"));
        if (userId == null || comment.getAuthor().getId().longValue() != userId.longValue()) {
            throw new NotAvailableException("Only sender can update!");
        }
        if (entryDto.getText() != null) {
            comment.setText(entryDto.getText());
            comment.setUpdated(LocalDateTime.now());
        }
        return setUsefulness(toCommentDto(commentRepository.save(comment)));
    }

    public CommentAdminDto getAdminCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Not Found!"));
        return setUsefulnessAdmin(toAdminDto(comment));
    }

    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Not Found!"));
        return setUsefulness(toCommentDto(comment));
    }

    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Not Found!"));
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentByOwner(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Not Found!"));
        if (userId == null || comment.getAuthor().getId().longValue() != userId.longValue()) {
            throw new NotAvailableException("Only sender or admin can delete it!");
        }
        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getCommentsByEvent(Long eventId, SortValue sortValue, Boolean asc, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        if (sortValue.equals(SortValue.DATE)) {
            if (asc) {
                return commentRepository.findAllByEventId(eventId, pageable).stream()
                        .map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
            }
            pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
            return commentRepository.findAllByEventId(eventId, pageable).stream()
                    .map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
        } else if (sortValue.equals(SortValue.USEFULNESS)) {
            List<CommentDto> comments = commentRepository.findAllByEventId(eventId, pageable)
                    .stream().map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
            return setSortByUsefulness(comments, asc);
        }
        throw new NotAvailableException("No such sort");
    }

    public List<CommentDto> getCommentsByUser(Long userId, SortValue sortValue, Boolean asc,
                                              Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        if (sortValue.equals(SortValue.DATE)) {
            if (asc) {
                return commentRepository.findAllByAuthorId(userId, pageable).stream()
                        .map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
            } else {
                pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
                return commentRepository.findAllByAuthorId(userId, pageable).stream()
                        .map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
            }
        } else if (sortValue.equals(SortValue.USEFULNESS)) {
            List<CommentDto> comments = commentRepository.findAllByAuthorId(userId, pageable)
                    .stream().map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
            return setSortByUsefulness(comments, asc);
        }
        throw new NotAvailableException("No such sort");
    }

    public List<CommentDto> getCommentsByEventInitiator(Long userId, SortValue sortValue, Boolean asc,
                                                        Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        if (sortValue.equals(SortValue.DATE)) {
            if (asc) {
                return commentRepository.findAllByEventInitiatorId(userId, pageable).stream()
                        .map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
            }
            pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
            return commentRepository.findAllByEventInitiatorId(userId, pageable).stream()
                    .map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
        } else if (sortValue.equals(SortValue.USEFULNESS)) {
            List<CommentDto> comments = commentRepository.findAllByEventInitiatorId(userId, pageable)
                    .stream().map(CommentMapper::toCommentDto).map(this::setUsefulness).collect(Collectors.toList());
            return setSortByUsefulness(comments, asc);
        }
        throw new NotAvailableException("No such sort");
    }

    /**
     * {@link #likeComment(Long, Long) setLikeToComment} method
     * При наличии лайка, он снимается при повторном нажатии. Рейтнг коммента становится +1 -> 0
     * Если юзер ставит лайк, а потом нажимает на дизлайк, то рейтинг будет +1 -> -1,
     * => Мы можем убрать лайк, либо отзеркалить его
     */
    public CommentDto likeComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Not Found!"));
        CommentLike like = likeRepository.findByCommentIdAndAuthorId(commentId, userId);
        if (like != null && like.getIslike().equals(true)) {
            likeRepository.deleteById(like.getId());
            return setUsefulness(toCommentDto(comment));
        } else if (like != null && like.getIslike().equals(false)) {
            return reverseLike(comment, like);
        }
        return addNewLike(comment, userId);
    }

    public CommentDto dislikeComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Not Found!"));
        CommentLike like = likeRepository.findByCommentIdAndAuthorId(commentId, userId);
        if (like != null && like.getIslike().equals(false)) {
            likeRepository.deleteById(like.getId());
            return setUsefulness(toCommentDto(comment));
        } else if (like != null && like.getIslike().equals(true)) {
            return reverseLike(comment, like);
        }
        return addNewDislike(comment, userId);
    }

    private CommentDto reverseLike(Comment comment, CommentLike like) {
        like.setIslike(!like.getIslike());
        likeRepository.save(like);
        return setUsefulness(toCommentDto(comment));
    }

    private CommentDto addNewLike(Comment comment, Long userId) {
        if (comment.getAuthor().getId().longValue() == userId.longValue()) {
            throw new NotAvailableException("");
        }
        likeRepository.save(CommentLike.builder().commentId(comment.getId()).authorId(userId).islike(true).build());
        return setUsefulness(toCommentDto(comment));
    }

    private CommentDto addNewDislike(Comment comment, Long userId) {
        if (comment.getAuthor().getId().longValue() == userId.longValue()) {
            throw new NotAvailableException("");
        }
        likeRepository.save(CommentLike.builder().commentId(comment.getId()).authorId(userId).islike(false).build());
        return setUsefulness(toCommentDto(comment));
    }

    private CommentDto setUsefulness(CommentDto commentDto) {
        Long like = likeRepository.countByCommentIdAndIslike(commentDto.getId(), true);
        Long dislike = likeRepository.countByCommentIdAndIslike(commentDto.getId(), false);
        commentDto.setUsefulness(like - dislike);
        return commentDto;
    }

    private CommentAdminDto setUsefulnessAdmin(CommentAdminDto commentDto) {
        Long like = likeRepository.countByCommentIdAndIslike(commentDto.getId(), true);
        Long dislike = likeRepository.countByCommentIdAndIslike(commentDto.getId(), false);
        commentDto.setUsefulness(like - dislike);
        return commentDto;
    }

    private List<CommentDto> setSortByUsefulness(List<CommentDto> comments, Boolean asc) {
        if (asc) {
            return comments.stream()
                    .sorted(Comparator.comparing(CommentDto::getUsefulness)).collect(Collectors.toList());
        }
        return comments.stream()
                .sorted(Comparator.comparing(CommentDto::getUsefulness).reversed()).collect(Collectors.toList());
    }
}
