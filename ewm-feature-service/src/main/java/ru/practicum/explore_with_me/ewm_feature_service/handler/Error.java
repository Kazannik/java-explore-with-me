package ru.practicum.explore_with_me.ewm_feature_service.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Error {
    String field;
    String message;

    @Override
    public String toString() {
        return String.format("Field: %s. Message: %s.", field, message);
    }
}

