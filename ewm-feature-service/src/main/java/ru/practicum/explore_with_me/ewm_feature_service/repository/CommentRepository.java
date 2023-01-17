package ru.practicum.explore_with_me.ewm_feature_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.ewm_feature_service.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}

