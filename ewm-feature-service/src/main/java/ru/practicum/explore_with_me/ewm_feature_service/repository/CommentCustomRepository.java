package ru.practicum.explore_with_me.ewm_feature_service.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.ewm_feature_service.model.Comment;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentSortEnum;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentStateEnum;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentCustomRepository {
    List<Comment> findComments(String text, Long userId, Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                               CommentStateEnum[] states, CommentSortEnum sort, int from, int size);
}

