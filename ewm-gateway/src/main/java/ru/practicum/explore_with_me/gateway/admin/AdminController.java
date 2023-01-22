package ru.practicum.explore_with_me.gateway.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.gateway.category.CategoryClient;
import ru.practicum.explore_with_me.gateway.category.dto.CategoryDto;
import ru.practicum.explore_with_me.gateway.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.gateway.client.RoleEnum;
import ru.practicum.explore_with_me.gateway.comment.CommentClient;
import ru.practicum.explore_with_me.gateway.compilation.CompilationClient;
import ru.practicum.explore_with_me.gateway.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.gateway.event.EventClient;
import ru.practicum.explore_with_me.gateway.event.dto.EventRequestDto;
import ru.practicum.explore_with_me.gateway.user.UserClient;
import ru.practicum.explore_with_me.gateway.user.dto.NewUserRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String CATEGORIES_API_PREFIX = "/categories";
    private static final String COMPILATIONS_API_PREFIX = "/compilations";
    private static final String COMPILATIONS_ID_API_PREFIX = COMPILATIONS_API_PREFIX + "/{compId}";
    private static final String EVENT_API_PREFIX = "/events";
    private static final String EVENT_ID_API_PREFIX = EVENT_API_PREFIX + "/{eventId}";
    private static final String COMPILATIONS_EVENT_ID_API_PREFIX = COMPILATIONS_ID_API_PREFIX + EVENT_ID_API_PREFIX;
    private static final String USERS_API_PREFIX = "/users";

    private final CategoryClient categoryClient;
    private final CompilationClient compilationClient;
    private final EventClient eventClient;
    private final UserClient userClient;
    private final CommentClient commentClient;


    @PostMapping(CATEGORIES_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createCategory(@RequestBody(required = false) NewCategoryDto requestDto) {
        log.info("Создание категории {}", requestDto);
        return categoryClient.createCategory(requestDto, RoleEnum.ADMINISTRATOR);
    }

    @PatchMapping(CATEGORIES_API_PREFIX)
    public ResponseEntity<Object> editCategory(@RequestBody(required = false) CategoryDto requestDto) {
        log.info("Правка категории {}", requestDto);
        return categoryClient.editCategory(requestDto, RoleEnum.ADMINISTRATOR);
    }

    @DeleteMapping(CATEGORIES_API_PREFIX + "/{catId}")
    public ResponseEntity<Object> removeCategory(@PathVariable Long catId) {
        log.info("Удаление категории, catId={}", catId);
        return categoryClient.removeCategory(catId, RoleEnum.ADMINISTRATOR);
    }

    @PostMapping(COMPILATIONS_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createCompilation(@RequestBody(required = false) NewCompilationDto requestDto) {
        log.info("Создание подборки {}", requestDto);
        return compilationClient.createCompilation(requestDto, RoleEnum.ADMINISTRATOR);
    }

    @DeleteMapping(COMPILATIONS_ID_API_PREFIX)
    public ResponseEntity<Object> removeCompilation(@PathVariable Long compId) {
        log.info("Удаление подборки, compId={}", compId);
        return compilationClient.removeCompilation(compId, RoleEnum.ADMINISTRATOR);
    }

    @PatchMapping(COMPILATIONS_ID_API_PREFIX + "/pin")
    public ResponseEntity<Object> pinCompilation(@PathVariable Long compId) {
        log.info("Закрепление подборки, compId={}", compId);
        return compilationClient.pinCompilation(compId, RoleEnum.ADMINISTRATOR);
    }

    @DeleteMapping(COMPILATIONS_ID_API_PREFIX + "/pin")
    public ResponseEntity<Object> unpinCompilation(@PathVariable Long compId) {
        log.info("Открепление подборки, compId={}", compId);
        return compilationClient.unpinCompilation(compId, RoleEnum.ADMINISTRATOR);
    }

    @PatchMapping(COMPILATIONS_EVENT_ID_API_PREFIX)
    public ResponseEntity<Object> addEvent(@PathVariable Long compId,
                                           @PathVariable Long eventId) {
        log.info("Добавление события к подборке, eventId={}, compId={}", eventId, compId);
        return compilationClient.addEvent(compId, eventId, RoleEnum.ADMINISTRATOR);
    }

    @DeleteMapping(COMPILATIONS_EVENT_ID_API_PREFIX)
    public ResponseEntity<Object> removeEvent(@PathVariable Long compId,
                                              @PathVariable Long eventId) {
        log.info("Удаление события из подборки, eventId={}, compId={}", eventId, compId);
        return compilationClient.removeEvent(compId, eventId, RoleEnum.ADMINISTRATOR);
    }

    @GetMapping(EVENT_API_PREFIX)
    public ResponseEntity<Object> findEvens(@RequestParam(required = false) Long[] users,
                                            @RequestParam(required = false) String[] states,
                                            @RequestParam(required = false) Long[] categories,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("Поиск всех событий (с параметрами)");
        return eventClient.findEvent(users, states, categories, rangeStart, rangeEnd, from, size,
                RoleEnum.ADMINISTRATOR);
    }

    @PatchMapping(EVENT_ID_API_PREFIX + "/publish")
    public ResponseEntity<Object> publishEvent(@PathVariable Long eventId) {
        log.info("Публикация события {}", eventId);
        return eventClient.publishEvent(eventId, RoleEnum.ADMINISTRATOR);
    }

    @PatchMapping(EVENT_ID_API_PREFIX + "/reject")
    public ResponseEntity<Object> rejectEvent(@PathVariable Long eventId) {
        log.info("Отклонение публикации события {}", eventId);
        return eventClient.rejectEvent(eventId, RoleEnum.ADMINISTRATOR);
    }

    @PutMapping(EVENT_ID_API_PREFIX)
    public ResponseEntity<Object> editEvent(@RequestBody EventRequestDto eventDto,
                                            @PathVariable Long eventId) {
        log.info("Правка события {}", eventId);
        return eventClient.editEvent(eventDto, eventId, RoleEnum.ADMINISTRATOR);
    }

    @RequestMapping(value = USERS_API_PREFIX, method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@RequestBody(required = false) NewUserRequestDto userDto) {
        log.info("Создание пользователя {}", userDto);
        return userClient.createUser(userDto, RoleEnum.ADMINISTRATOR);
    }

    @GetMapping(USERS_API_PREFIX)
    public ResponseEntity<Object> findUsers(@RequestParam(required = false) Long[] ids,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("Поиск всех пользователей (с параметрами)");
        return userClient.findUser(ids, from, size, RoleEnum.ADMINISTRATOR);
    }

    @DeleteMapping(USERS_API_PREFIX + "/{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable Long userId) {
        log.info("Удаление пользователя, userId={}", userId);
        return userClient.removeUser(userId, RoleEnum.ADMINISTRATOR);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<Object> getComment(@PathVariable Long commentId) {
        log.info("Получение комментария по его индексу, Id={}", commentId);
        return commentClient.getComment(commentId, RoleEnum.ADMINISTRATOR);
    }

    @PatchMapping("/comments/{commentId}/publish")
    public ResponseEntity<Object> publishComment(@PathVariable Long commentId) {
        log.info("Публикация комментария, Id={}", commentId);
        return commentClient.publishComment(commentId, RoleEnum.ADMINISTRATOR);
    }

    @PatchMapping("/comments/{commentId}/reject")
    public ResponseEntity<Object> rejectComment(@PathVariable Long commentId) {
        log.info("Отклонение комментария, Id={}", commentId);
        return commentClient.rejectComment(commentId, RoleEnum.ADMINISTRATOR);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Object> removeComment(@PathVariable Long commentId) {
        log.info("Удаление комментария, Id={}", commentId);
        return commentClient.removeComment(commentId, RoleEnum.ADMINISTRATOR);
    }

    @GetMapping("/comments")
    public ResponseEntity<Object> findComments(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) Long eventId,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(required = false) String[] state,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        return commentClient.findComments(text, eventId, rangeStart, rangeEnd, state, sort, from, size,
                RoleEnum.ADMINISTRATOR);
    }
}
