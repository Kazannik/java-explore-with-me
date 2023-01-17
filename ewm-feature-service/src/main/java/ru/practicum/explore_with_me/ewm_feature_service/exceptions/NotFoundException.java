package ru.practicum.explore_with_me.ewm_feature_service.exceptions;

public class NotFoundException extends BaseException {
    public NotFoundException(String field, String message, String reason) {
        super(field, message, reason);
    }

}

