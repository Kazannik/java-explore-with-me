package ru.practicum.explore_with_me.ewm_feature_service.model;

public enum CommentStateEnum {
    PENDING,
    PUBLISHED,
    REJECT;

    public static CommentStateEnum of(String state) {
        for (CommentStateEnum command : values()) {
            if (command.name().equalsIgnoreCase(state)) {
                return command;
            }
        }
        return CommentStateEnum.PENDING;
    }

    public static CommentStateEnum[] of(String[] states) {
        if (states != null) {
            CommentStateEnum[] result = new CommentStateEnum[states.length];
            for (int i = 0; i < states.length; i++) {
                result[i] = CommentStateEnum.of(states[i]);
            }
            return result;
        } else {
            return new CommentStateEnum[0];
        }
    }
}

