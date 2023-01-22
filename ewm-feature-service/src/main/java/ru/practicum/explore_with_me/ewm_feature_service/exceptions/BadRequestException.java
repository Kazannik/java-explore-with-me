package ru.practicum.explore_with_me.ewm_feature_service.exceptions;

public class BadRequestException extends BaseException {

    public BadRequestException(String field, String message, String reason) {
        super(field, message, reason);
    }

}

