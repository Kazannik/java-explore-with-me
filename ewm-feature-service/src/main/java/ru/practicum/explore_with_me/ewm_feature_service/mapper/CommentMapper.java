package ru.practicum.explore_with_me.ewm_feature_service.mapper;

import ru.practicum.explore_with_me.ewm_feature_service.dto.CommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.FullCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.NewCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.model.Comment;

public interface CommentMapper {
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    Comment toComment(NewCommentDto dto);

    Comment toComment(CommentDto dto);

    CommentDto toDto(Comment comment);

    FullCommentDto toDto(Comment comment, Long userId);
}
