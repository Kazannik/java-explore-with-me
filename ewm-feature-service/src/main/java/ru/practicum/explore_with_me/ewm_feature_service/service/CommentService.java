package ru.practicum.explore_with_me.ewm_feature_service.service;

import ru.practicum.explore_with_me.ewm_feature_service.dto.CommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.FullCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.NewCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.model.Comment;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentSortEnum;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentStateEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface CommentService {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    Comment createComment(Comment comment, Long userId);

    CommentDto createComment(NewCommentDto commentDto, Long userId, String role);

    Comment getComment(Long commentId);

    CommentDto getCommentToDto(Long commentId, Long userId, String role);

    Comment editComment(Long commentId, Comment comment, Long userId);

    CommentDto editCommentToDto(Long commentId, CommentDto commentDto, Long userId, String role);

    Comment publishComment(Long commentId);

    CommentDto publishComment(Long commentId, String role);

    Comment rejectComment(Long commentId);

    CommentDto rejectComment(Long commentId, String role);

    void removeComment(Comment comment);

    void removeComment(Long commentId);

    void removeComment(Long commentId, Long userId, String role);

    Comment likeComment(Long commentId, Long userId);

    FullCommentDto unknownLikeCommentToDto(Long commentId, Long userId, String role);

    Comment unknownLikeComment(Long commentId, Long userId);

    FullCommentDto likeCommentToDto(Long commentId, Long userId, String role);

    Comment dislikeComment(Long commentId, Long userId);

    FullCommentDto dislikeCommentToDto(Long commentId, Long userId, String role);

    List<Comment> findComments(String text, Long userId, Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                               CommentStateEnum[] states, CommentSortEnum sort, Integer from, Integer size);

    List<CommentDto> findComments(String text, Long userId, Long eventId, String rangeStart, String rangeEnd,
                                      String[] states, String sort, Integer from, Integer size, String role);

    List<Comment> findComments(String text, Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                               CommentSortEnum sort, Integer from, Integer size);

    List<CommentDto> findComments(String text, Long eventId, String rangeStart, String rangeEnd, String sort,
                                  Integer from, Integer size);
}

