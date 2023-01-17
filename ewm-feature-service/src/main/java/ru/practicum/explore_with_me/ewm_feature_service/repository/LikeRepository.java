package ru.practicum.explore_with_me.ewm_feature_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.ewm_feature_service.model.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

}

