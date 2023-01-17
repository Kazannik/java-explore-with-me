package ru.practicum.explore_with_me.ewm_feature_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.ewm_feature_service.client.MainServiceClient;
import ru.practicum.explore_with_me.ewm_feature_service.dto.*;
import ru.practicum.explore_with_me.ewm_feature_service.exceptions.AccessForbiddenException;
import ru.practicum.explore_with_me.ewm_feature_service.exceptions.BadRequestException;
import ru.practicum.explore_with_me.ewm_feature_service.exceptions.NotFoundException;
import ru.practicum.explore_with_me.ewm_feature_service.mapper.CommentMapper;
import ru.practicum.explore_with_me.ewm_feature_service.model.Comment;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentStateEnum;
import ru.practicum.explore_with_me.ewm_feature_service.model.*;
import ru.practicum.explore_with_me.ewm_feature_service.repository.CommentCustomRepository;
import ru.practicum.explore_with_me.ewm_feature_service.repository.CommentRepository;
import ru.practicum.explore_with_me.ewm_feature_service.repository.LikeRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentCustomRepository commentCustomRepository;
    private final LikeRepository likeRepository;
    private final CommentMapper commentMapper;
    private final MainServiceClient mainServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Comment createComment(Comment comment, Long userId) {
        EventSysDto event = getEventOrThrow(comment.getEventId());
        UserDto user = getUserOrThrow(userId);
        if (event.getUserId().equals(user.getId())) {
            throw new BadRequestException(this.getClass().getName(),
                    "The user cannot comment on their own events",
                    "Пользователь на может комментировать собственные события.");
        } else if (!EventStateEnum.of(event.getState()).equals(EventStateEnum.PUBLISHED)) {
            throw new BadRequestException(this.getClass().getName(),
                    "The user cannot comment on unpublished events",
                    "Пользователь на может комментировать неопубликованные события.");
        } else {
            return commentRepository.save(comment);
        }
    }

    @Override
    public CommentDto createComment(@Valid @NotNull NewCommentDto commentDto, Long userId, String role) {
        if (RoleEnum.of(role) == RoleEnum.USER) {
            return commentMapper.toDto(createComment(commentMapper.toComment(commentDto), userId));
        } else {
            throw new AccessForbiddenException(this.getClass().getName(),
                    String.format("Only User can create Comment {}", commentDto),
                    "Только пользователь может создавать комментарий.");
        }
    }

    @Override
    public Comment getComment(Long commentId) {
        return getCommentOrThrow(commentId);
    }

    @Override
    public CommentDto getCommentToDto(Long commentId, Long userId, String role) {
        Comment comment = getComment(commentId);
        if (RoleEnum.of(role) == RoleEnum.ADMINISTRATOR) {
            return commentMapper.toDto(comment);
        } else if (RoleEnum.of(role) == RoleEnum.USER &&
                comment.getUserId().equals(userId)) {
            return commentMapper.toDto(comment, userId);
        } else if (RoleEnum.of(role) == RoleEnum.PUBLIC &&
                comment.getState().equals(CommentStateEnum.PUBLISHED)) {
            return commentMapper.toDto(comment);
        } else {
            throw new AccessForbiddenException(this, role);
        }
    }

    @Override
    @Transactional
    public Comment editComment(Long commentId, Comment comment, Long userId) {
        if (comment.getState().equals(CommentStateEnum.PUBLISHED)) {
            throw new AccessForbiddenException(this.getClass().getName(),
                    "State of comment must not be PUBLISHED",
                    "Комментарий опубликован, его больше нельзя редактировать.");
        }
        Comment oldComment = getCommentOrThrow(commentId);
        if (!oldComment.getUserId().equals(userId)) {
            throw new AccessForbiddenException(this.getClass().getName(),
                    String.format("Only author can edit Comment Id{}", comment),
                    "Только автор может редактировать свой комментарий.");
        } else if (comment.getDescription() != null) {
            oldComment.setDescription(oldComment.getDescription());
            oldComment.setCreatedDate(LocalDateTime.now());
            log.debug("{} has been update.", comment);
            return commentRepository.save(oldComment);
        } else {
            return oldComment;
        }
    }

    @Override
    public CommentDto editCommentToDto(Long commentId, CommentDto commentDto, Long userId, String role) {
        if (RoleEnum.of(role) == RoleEnum.USER) {
            return commentMapper.toDto(editComment(commentId, commentMapper.toComment(commentDto), userId));
        } else {
            throw new AccessForbiddenException(this.getClass().getName(),
                    String.format("Only User can edit Comment Id{}", commentDto),
                    "Только пользователь-автор может редактировать комментарий.");
        }
    }

    @Override
    public Comment publishComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        if (!comment.getState().equals(CommentStateEnum.PENDING)) {
            throw new BadRequestException(this.getClass().getName(),
                    "State of comment must be PENDING",
                    String.format("Комментарий id%d имеет статус %s, в то время когда должен быть PENDING.",
                            commentId, comment.getState()));
        }
        comment.setState(CommentStateEnum.PUBLISHED);
        return commentRepository.save(comment);
    }

    @Override
    public CommentDto publishComment(Long commentId, String role) throws AccessForbiddenException {
        if (RoleEnum.of(role) == RoleEnum.ADMINISTRATOR) {
            return commentMapper.toDto(publishComment(commentId));
        } else {
            throw new AccessForbiddenException(this, role);
        }
    }

    @Override
    public Comment rejectComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        if (!comment.getState().equals(CommentStateEnum.PENDING)) {
            throw new BadRequestException(this.getClass().getName(),
                    "State of comment must be not PENDING",
                    String.format("Comment id%d has state %s.", commentId, comment.getState()));
        }
        comment.setState(CommentStateEnum.REJECT);
        return commentRepository.save(comment);
    }

    @Override
    public CommentDto rejectComment(Long commentId, String role) throws AccessForbiddenException {
        if (RoleEnum.of(role) == RoleEnum.ADMINISTRATOR) {
            return commentMapper.toDto(rejectComment(commentId));
        } else {
            throw new AccessForbiddenException(this, role);
        }
    }

    @Override
    @Transactional
    public void removeComment(Comment comment) {
        likeRepository.deleteAll(comment.getLikes());
        comment.setLikes(null);
        commentRepository.delete(comment);
    }

    @Override
    public void removeComment(Long commentId) {
        removeComment(getCommentOrThrow(commentId));
    }

    @Override
    public void removeComment(Long commentId, Long userId, String role) {
        Comment comment = getCommentOrThrow(commentId);
        if (RoleEnum.of(role) == RoleEnum.ADMINISTRATOR ||
                ((RoleEnum.of(role) == RoleEnum.USER && comment.getUserId().equals(userId)))) {
            removeComment(comment);
        } else {
            throw new AccessForbiddenException(this.getClass().getName(),
                    String.format("Only author (id {}) or Administrator can remove Comment Id{}",
                            userId, commentId),
                    "Только автор комментария или администратор может удалить комментарий.");
        }
    }

    @Override
    public Comment likeComment(Long commentId, Long userId) {
        return setLike(commentId, userId, CommentLikeEnum.LIKE);
    }

    @Override
    public FullCommentDto unknownLikeCommentToDto(Long commentId, Long userId, String role) {
        if (RoleEnum.of(role) == RoleEnum.USER) {
            Comment comment = unknownLikeComment(commentId, userId);
            return commentMapper.toDto(comment, userId);
        } else {
            throw new AccessForbiddenException(this, role);
        }
    }

    @Override
    public Comment unknownLikeComment(Long commentId, Long userId) {
        return setLike(commentId, userId, CommentLikeEnum.UNKNOWN);
    }

    @Override
    public FullCommentDto likeCommentToDto(Long commentId, Long userId, String role) {
        if (RoleEnum.of(role) == RoleEnum.USER) {
            Comment comment = likeComment(commentId, userId);
            return commentMapper.toDto(comment, userId);
        } else {
            throw new AccessForbiddenException(this, role);
        }
    }

    @Override
    public Comment dislikeComment(Long commentId, Long userId) {
        return setLike(commentId, userId, CommentLikeEnum.DISLIKE);
    }

    @Override
    public FullCommentDto dislikeCommentToDto(Long commentId, Long userId, String role) {
        if (RoleEnum.of(role) == RoleEnum.USER) {
            Comment comment = dislikeComment(commentId, userId);
            return commentMapper.toDto(comment, userId);
        } else {
            throw new AccessForbiddenException(this, role);
        }
    }

    @Override
    public List<Comment> findComments(String text, Long userId, Long eventId, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, CommentStateEnum[] states, CommentSortEnum sort,
                                      Integer from, Integer size) {
        return commentCustomRepository.findComments(text, userId, eventId, rangeStart, rangeEnd, states, sort,
                from, size);
    }

    @Override
    public List<CommentDto> findComments(String text, Long userId, Long eventId, String rangeStart, String rangeEnd,
                                         String[] states, String sort, Integer from, Integer size, String role) {

        switch (RoleEnum.of(role)) {
            case ADMINISTRATOR:
                return findComments(text, userId, eventId,
                        rangeStart != null ? LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER) : null,
                        rangeEnd != null ? LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER) : null,
                        states != null ? CommentStateEnum.of(states) : null,
                        sort != null ? CommentSortEnum.valueOf(sort) : null,
                        from, size).stream()
                        .map(c -> commentMapper.toDto(c))
                        .collect(Collectors.toList());
            case USER:
                return findComments(text, null, eventId,
                        rangeStart != null ? LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER) : null,
                        rangeEnd != null ? LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER) : null,
                        states != null ? CommentStateEnum.of(states) : null,
                        sort != null ? CommentSortEnum.valueOf(sort) : null,
                        from, size).stream()
                        .map(c -> commentMapper.toDto(c, userId))
                        .collect(Collectors.toList());
            default:
                return findComments(text, eventId, rangeStart, rangeEnd, sort, from, size);
        }
    }

    @Override
    public List<Comment> findComments(String text, Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      CommentSortEnum sort, Integer from, Integer size) {
        return commentCustomRepository.findComments(text, null, eventId, rangeStart, rangeEnd,
                new CommentStateEnum[]{CommentStateEnum.PUBLISHED}, sort, from, size);
    }

    @Override
    public List<CommentDto> findComments(String text, Long eventId, String rangeStart, String rangeEnd, String sort,
                                         Integer from, Integer size) {
        return findComments(text, eventId,
                rangeStart != null ? LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER) : null,
                rangeEnd != null ? LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER) : null,
                sort != null ? CommentSortEnum.valueOf(sort) : null, from, size).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    private Comment setLike(Long commentId, Long userId, CommentLikeEnum like) {
        Comment comment = getCommentOrThrow(commentId);
        if (!comment.getState().equals(CommentStateEnum.PUBLISHED)) {
            throw new BadRequestException(this.getClass().getName(),
                    "Оценен может быть только опубликованный комментарий.",
                    String.format("Комментарий id%d не имеет статус %s.",
                            commentId, comment.getState().name()));
        } else if (!comment.getUserId().equals(userId)) {
            Optional<Like> likeComment = comment.getLikes()
                    .stream()
                    .filter(l -> l.getUserId().equals(userId)).findFirst();
            if ((likeComment.isEmpty() && like.equals(CommentLikeEnum.UNKNOWN)) ||
                    (likeComment.isPresent() && likeComment.get().getLike().equals(like))) {
                throw new BadRequestException(this.getClass().getName(),
                        "Ранее пользователь уже сделал подобную отметку.",
                        String.format("Комментарий id%d не может быть повторно отмечен %s.",
                                commentId, likeComment.get().getLike().name()));
            } else if (likeComment.isPresent() && like.equals(CommentLikeEnum.UNKNOWN)) {
                likeRepository.delete(likeComment.get());
                comment.removeLike(likeComment.get());
            } else if (likeComment.isPresent()) {
                likeComment.get().setLike(like);
                likeRepository.save(likeComment.get());
            } else {
                Like newLike = new Like(commentId, userId, like);
                comment.addLike(newLike);
                likeRepository.save(newLike);
            }
            log.debug("{} has been likes update.", comment);
            return comment;
        } else {
            throw new AccessForbiddenException(this.getClass().getName(),
                    String.format("Only users can edit Comment Id{}", comment),
                    "Только пользователь может поставить лайк/дизлайк только чужому комментарию, либо снять его.");
        }
    }

    private UserDto getUserOrThrow(Long userId) {
        Object objects = mainServiceClient
                .getUser(userId, ru.practicum.explore_with_me.ewm_feature_service.client.RoleEnum.ADMINISTRATOR)
                .getBody();
        if (objects != null) {
            return objectMapper.convertValue(objects, UserDto.class);
        } else {
            throw new NotFoundException(this.getClass().getName(),
                    String.format("User %d not found.", userId),
                    "Пользователь с заданным индексом отсутствует.");
        }
    }

    private EventSysDto getEventOrThrow(Long eventId) {
        Object objects = mainServiceClient
                .getEvent(eventId, ru.practicum.explore_with_me.ewm_feature_service.client.RoleEnum.ADMINISTRATOR)
                .getBody();
        if (objects != null) {
            return objectMapper.convertValue(objects, EventSysDto.class);
        } else {
            throw new NotFoundException(this.getClass().getName(),
                    String.format("Event %d not found.", eventId),
                    "Событие с заданным индексом отсутствует.");
        }
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(this.getClass().getName(),
                String.format("Comment %d not found.", commentId),
                "Комментарий с заданным индексом отсутствует."));
    }
}

