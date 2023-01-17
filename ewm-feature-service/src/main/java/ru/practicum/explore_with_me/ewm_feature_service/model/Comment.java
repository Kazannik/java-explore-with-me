package ru.practicum.explore_with_me.ewm_feature_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "created_date", nullable = false)
    LocalDateTime createdDate;

    @NotBlank
    @Column(length = 2000, nullable = false)
    String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "state_enum")
    CommentStateEnum state = CommentStateEnum.PENDING;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "comment_id")
    List<Like> likes = new ArrayList<>();

    @Transient
    Long rating = getTotal();

    public Long getRating() {
        rating = getTotal();
        return rating;
    }

    public void addLike(Like like) {
        likes.add(like);
    }

    public void removeLike(Like like) {
        likes.remove(like);
    }

    private long getTotal() {
        return likes.stream()
                .mapToLong(l -> l.getLike().value)
                .sum();
    }
}

