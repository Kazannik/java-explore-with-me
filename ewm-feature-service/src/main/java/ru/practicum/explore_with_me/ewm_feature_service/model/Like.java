package ru.practicum.explore_with_me.ewm_feature_service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "comment_id", nullable = false)
    Long commentId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "like_enum")
    CommentLikeEnum like = CommentLikeEnum.UNKNOWN;

    public Like(Long commentId, Long userId, CommentLikeEnum like) {
        this.commentId = commentId;
        this.userId = userId;
        this.like = like;
    }
}

