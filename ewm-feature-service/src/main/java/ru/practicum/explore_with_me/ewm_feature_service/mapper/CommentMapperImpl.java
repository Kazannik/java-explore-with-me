package ru.practicum.explore_with_me.ewm_feature_service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.ewm_feature_service.dto.CommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.FullCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.NewCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.model.Comment;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentLikeEnum;
import ru.practicum.explore_with_me.ewm_feature_service.model.CommentStateEnum;
import ru.practicum.explore_with_me.ewm_feature_service.model.Like;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class CommentMapperImpl implements CommentMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @Override
    public Comment toComment(NewCommentDto dto) {
        if (dto == null) {
            return null;
        }
        Comment comment = new Comment();

        if (dto.getCreatedDate() != null) {
            comment.setCreatedDate(LocalDateTime.parse(dto.getCreatedDate(), dateTimeFormatter));
        }
        comment.setUserId(dto.getUserId());
        comment.setEventId(dto.getEventId());
        comment.setDescription(dto.getDescription());
        comment.setState(CommentStateEnum.PENDING);
        return comment;
    }

    @Override
    public Comment toComment(CommentDto dto) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();

        if (dto.getCreatedDate() != null) {
            comment.setCreatedDate(LocalDateTime.parse(dto.getCreatedDate(), dateTimeFormatter));
        }
        comment.setId(dto.getId());
        comment.setUserId(dto.getUserId());
        comment.setEventId(dto.getEventId());
        comment.setDescription(dto.getDescription());
        if (dto.getState() != null) {
            comment.setState(CommentStateEnum.of(dto.getState()));
        }

        return comment;
    }

    @Override
    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto dto = new CommentDto();
        setValueToDto(dto, comment);
        return dto;
    }

    @Override
    public FullCommentDto toDto(Comment comment, Long userId) {
        if (comment == null) {
            return null;
        }

        FullCommentDto dto = new FullCommentDto();
        setValueToDto(dto, comment);

        Optional<Like> userLikes = comment.getLikes().stream()
                .filter(l -> l.getUserId().equals(userId))
                .findFirst();
        dto.setMyLike(userLikes.isPresent() ? userLikes.get().getLike().name() : CommentLikeEnum.UNKNOWN.name());
        return dto;
    }

    private static void setValueToDto(CommentDto dto, Comment comment) {
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setEventId(comment.getEventId());
        dto.setCreatedDate(comment.getCreatedDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        dto.setDescription(comment.getDescription());
        dto.setState(comment.getState().name());
        dto.setRating(comment.getRating());
    }
}

