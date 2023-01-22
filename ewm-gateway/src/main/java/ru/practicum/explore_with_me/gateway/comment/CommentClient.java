package ru.practicum.explore_with_me.gateway.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore_with_me.gateway.client.BaseClient;
import ru.practicum.explore_with_me.gateway.client.PathBuilder;
import ru.practicum.explore_with_me.gateway.client.RoleEnum;
import ru.practicum.explore_with_me.gateway.comment.dto.NewCommentDto;

@Service
public class CommentClient extends BaseClient {
    private static final String API_PREFIX = "/comments";

    @Autowired
    CommentClient(@Value("${server.feature.url}") String serverUrl,
                  RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> createComment(NewCommentDto commentDto, Long userId, RoleEnum role) {
        return post("", userId, role, commentDto);
    }

    public ResponseEntity<Object> getComment(Long commentId, Long userId, RoleEnum role) {
        return get("/" + commentId, userId, role);
    }

    public ResponseEntity<Object> getComment(Long commentId, RoleEnum role) {
        return get("/" + commentId, role);
    }

    public ResponseEntity<Object> editComment(NewCommentDto commentDto, Long commentId, Long userId, RoleEnum role) {
        return put("/" + commentId, userId, role, commentDto);
    }

    public ResponseEntity<Object> publishComment(Long commentId, RoleEnum role) {
        return patch("/" + commentId + "/publish", role);
    }

    public ResponseEntity<Object> rejectComment(Long commentId, RoleEnum role) {
        return patch("/" + commentId + "/reject", role);
    }

    public ResponseEntity<Object> removeComment(Long commentId, Long userId, RoleEnum role) {
        return delete("/" + commentId, userId, role);
    }

    public ResponseEntity<Object> removeComment(Long commentId, RoleEnum role) {
        return delete("/" + commentId, role);
    }

    public ResponseEntity<Object> likeComment(Long commentId, Long userId, RoleEnum role) {
        return patch("/" + commentId + "/like", userId, role);
    }

    public ResponseEntity<Object> unknownComment(Long commentId, Long userId, RoleEnum role) {
        return patch("/" + commentId + "/unknown", userId, role);
    }

    public ResponseEntity<Object> dislikeComment(Long commentId, Long userId, RoleEnum role) {
        return patch("/" + commentId + "/dislike", userId, role);
    }

    public ResponseEntity<Object> findComments(String text, Long userId, Long eventId, String rangeStart,
                                               String rangeEnd, String[] state, String sort,
                                               Integer from, Integer size, RoleEnum role) {
        PathBuilder pathBuilder = new PathBuilder();
        setValue(pathBuilder, text, eventId, rangeStart, rangeEnd, state, sort,from, size);

        if (pathBuilder.isPresent()) {
            return get(pathBuilder.getPath(), userId, role, pathBuilder.getParameters());
        } else {
            return get("", userId, role);
        }
    }

    public ResponseEntity<Object> findComments(String text, Long eventId, String rangeStart, String rangeEnd,
                                               String[] state, String sort, Integer from, Integer size, RoleEnum role) {
        PathBuilder pathBuilder = new PathBuilder();
        setValue(pathBuilder, text, eventId, rangeStart, rangeEnd, state, sort,from, size);

        if (pathBuilder.isPresent()) {
            return get(pathBuilder.getPath(), role, pathBuilder.getParameters());
        } else {
            return get("", role);
        }
    }

    private static void setValue(PathBuilder pathBuilder, String text, Long eventId, String rangeStart, String rangeEnd,
                                 String[] state, String sort, Integer from, Integer size) {
        pathBuilder.addParameter("text", text);
        pathBuilder.addParameter("eventId", eventId);
        pathBuilder.addParameter("rangeStart", rangeStart);
        pathBuilder.addParameter("rangeEnd", rangeEnd);
        pathBuilder.addParameter("state", state);
        pathBuilder.addParameter("sort", sort);
        pathBuilder.addParameter("from", from);
        pathBuilder.addParameter("size", size);
    }
}

