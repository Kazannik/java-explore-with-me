package ru.practicum.explore_with_me.ewm_feature_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore_with_me.ewm_feature_service.exceptions.*;

import javax.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException e) {
        log.error("Error: {}", e.getMessage());
        return new ApiError(e, StatusEnum.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final MethodArgumentNotValidException e) {
        log.error("Error: {}", e.getMessage());
        return new ApiError(e, "Ошибка валидации входных данных.", "Неверно заполнены поля.",
                StatusEnum.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final ConstraintViolationException e) {
        log.error("Error: {}", e.getMessage());
        return new ApiError(e, "Ошибка валидации входных данных.", "Неверно заполнены поля.",
                StatusEnum.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final DateTimeParseException e) {
        log.error("Error: {}", e.getMessage());
        return new ApiError(e, "Ошибка валидации входных данных.", "Неверно заполнены поля.",
                StatusEnum.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error("Error: {}", e.getMessage());
        return new ApiError(e, StatusEnum.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final AccessForbiddenException e) {
        log.error("Error: {}", e.getMessage());
        return new ApiError(e, StatusEnum.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerErrorException(final Throwable e) {
        log.error("Error: {}", e.getMessage());
        return new ApiError(e, e.getMessage(), StatusEnum.INTERNAL_SERVER_ERROR);
    }
}

