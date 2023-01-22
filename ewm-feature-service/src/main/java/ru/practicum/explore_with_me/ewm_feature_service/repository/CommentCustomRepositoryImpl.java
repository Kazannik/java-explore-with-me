package ru.practicum.explore_with_me.ewm_feature_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.ewm_feature_service.model.Comment;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentSortEnum;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentStateEnum;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final EntityManager entityManager;

    @Override
    public List<Comment> findComments(String text, Long userId, Long eventId,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      CommentStateEnum[] states, CommentSortEnum sort, int from, int size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);
        Root<Comment> commentRoot = criteriaQuery.from(Comment.class);

        List<Predicate> filterPredicates = new ArrayList<>();

        if (text != null && !text.isEmpty()) {
            filterPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(commentRoot.get("description")), "%" +
                    text.toLowerCase() + "%"));
        }

        if (userId != null) {
            filterPredicates.add(criteriaBuilder.isTrue(commentRoot.get("userId").in(userId)));
        }
        if (eventId != null) {
            filterPredicates.add(criteriaBuilder.isTrue(commentRoot.get("eventId").in(userId)));
        }
        if (rangeStart != null && rangeEnd != null) {
            filterPredicates.add(criteriaBuilder.between(commentRoot.get("createdDate"), rangeStart, rangeEnd));
        } else {
             filterPredicates.add(criteriaBuilder.greaterThan(commentRoot.get("createdDate"), criteriaBuilder.currentTimestamp()));
        }
        if (states != null && states.length != 0) {
            filterPredicates.add(criteriaBuilder.isTrue(commentRoot.get("state").in(states)));
        }

        criteriaQuery.select(commentRoot).where(criteriaBuilder.and(filterPredicates.toArray(new Predicate[]{})));

        switch (sort != null ? sort : CommentSortEnum.CREATED) {
            case USER:
                criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get("userId")));
                break;
            case EVENT:
                criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get("eventId")));
                break;
            case RATING:
                criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get("rating")));
                break;
            default:
                criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get("createdDate")));
        }

        TypedQuery<Comment> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(from + size);
        return typedQuery.getResultList();
    }
}

