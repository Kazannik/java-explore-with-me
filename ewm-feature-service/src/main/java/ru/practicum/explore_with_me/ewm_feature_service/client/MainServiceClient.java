package ru.practicum.explore_with_me.ewm_feature_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MainServiceClient {
    private static final String HEADER_USER_ROLE = "X-Explore-With-Me-User-Role";
    private static final RoleEnum DEFAULT_USER_ROLE = RoleEnum.PUBLIC;
    protected final RestTemplate rest;

    @Autowired
    MainServiceClient(@Value("${server.ewm.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<Object> getUser(Long userId, RoleEnum role) {
        return get("/users/" + userId, role);
    }

    public ResponseEntity<Object> getEvent(Long eventId, RoleEnum role) {
        return get("/events/" + eventId + "/sys", role);
    }

    protected ResponseEntity<Object> get(String path, RoleEnum role) {
        return get(path, role, null);
    }

    protected ResponseEntity<Object> get(String path, RoleEnum role, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, role, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, RoleEnum role,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity;
        if (body != null) {
            requestEntity = new HttpEntity<>(body, defaultHeaders(role));
        } else {
            requestEntity = new HttpEntity<>(defaultHeaders(role));
        }
        ResponseEntity<Object> response;
        try {
            if (parameters != null && parameters.size() > 0) {
                response = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {

            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(response);
    }

    private HttpHeaders defaultHeaders(RoleEnum role) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (role != null) {
            headers.set(HEADER_USER_ROLE, role.name());
        } else {
            headers.set(HEADER_USER_ROLE, DEFAULT_USER_ROLE.name());
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}

