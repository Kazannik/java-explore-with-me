package ru.practicum.explore_with_me.ewm_feature_service.model;

public enum CommentLikeEnum {
    DISLIKE(-1),
    UNKNOWN(0),
    LIKE(1);

    final long value;

    CommentLikeEnum(long value) {
        this.value = value;
    }

    public static CommentLikeEnum of(String value) {
        for (CommentLikeEnum position : values()) {
            if (position.name().equalsIgnoreCase(value)) {
                return position;
            }
        }
        return CommentLikeEnum.UNKNOWN;
    }
}

