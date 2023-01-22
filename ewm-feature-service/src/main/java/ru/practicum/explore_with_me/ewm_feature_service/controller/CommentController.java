package ru.practicum.explore_with_me.ewm_feature_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.ewm_feature_service.dto.CommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.FullCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.dto.NewCommentDto;
import ru.practicum.explore_with_me.ewm_feature_service.service.CommentService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private static final String HEADER_USER_ROLE = "X-Explore-With-Me-User-Role";
    private static final String HEADER_USER_ID = "X-Explore-With-Me-User-Id";
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@RequestBody NewCommentDto commentDto,
                                    @RequestHeader(HEADER_USER_ID) Long userId,
                                    @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        return commentService.createComment(commentDto, userId, role);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId,
                                 @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
                                 @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Получение комментария, id={}", commentId);
        return commentService.getCommentToDto(commentId, userId, role);
    }

    @PutMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long commentId,
                                  @RequestBody CommentDto commentDto,
                                  @RequestHeader(HEADER_USER_ID) Long userId,
                                  @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Правка комментария, id={}", commentDto.getId());
        return commentService.editCommentToDto(commentId, commentDto, userId, role);
    }

    @PatchMapping("/{commentId}/publish")
    public CommentDto publishComment(@PathVariable Long commentId,
                                     @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Публикация комментария, id={}", commentId);
        return commentService.publishComment(commentId, role);
    }

    @PatchMapping("/{commentId}/reject")
    public CommentDto rejectComment(@PathVariable Long commentId,
                                    @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Отклонение комментария комментария, id={}", commentId);
        return commentService.rejectComment(commentId, role);
    }

    @DeleteMapping("/{commentId}")
    public void removeComment(@PathVariable Long commentId,
                              @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
                              @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Удаление комментария, id={}", commentId);
        commentService.removeComment(commentId, userId, role);
    }

    @PatchMapping("/{commentId}/like")
    public FullCommentDto likeComment(@PathVariable Long commentId,
                                      @RequestHeader(HEADER_USER_ID) Long userId,
                                      @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Лайк к комментарию, id={}", commentId);
        return commentService.likeCommentToDto(commentId, userId, role);
    }

    @PatchMapping("/{commentId}/unknown")
    public FullCommentDto unknownComment(@PathVariable Long commentId,
                                         @RequestHeader(HEADER_USER_ID) Long userId,
                                         @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Снять отметку с комментария, id={}", commentId);
        return commentService.unknownLikeCommentToDto(commentId, userId, role);
    }

    @PatchMapping("/{commentId}/dislike")
    public FullCommentDto dislikeComment(@NotNull @PathVariable Long commentId,
                                         @RequestHeader(HEADER_USER_ID) Long userId,
                                         @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        log.info("Дизлайк к комментарию, id={}", commentId);
        return commentService.dislikeCommentToDto(commentId, userId, role);
    }

    @GetMapping
    public List<CommentDto> findComments(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) Long eventId,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false) String[] states,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size,
                                         @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
                                         @NotNull @RequestHeader(HEADER_USER_ROLE) String role) {
        return commentService.findComments(text, userId, eventId, rangeStart, rangeEnd, states, sort, from, size, role);
    }
}

