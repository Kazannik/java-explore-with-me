package ru.practicum.explore_with_me.gateway.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.gateway.client.RoleEnum;

@Slf4j
@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentClient commentClient;

    @GetMapping("/{commentId}")
    public ResponseEntity<Object> getComment(@PathVariable Long commentId) {
        log.info("Получение комментария по его индексу, Id={}", commentId);
        return commentClient.getComment(commentId, RoleEnum.PUBLIC);
    }

    @GetMapping()
    public ResponseEntity<Object> findComments(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) Long eventId,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        return commentClient.findComments(text, eventId, rangeStart, rangeEnd, new String[]{"PUBLISHED"}, sort,
                from, size, RoleEnum.PUBLIC);
    }
}
