package ru.practicum.explore_with_me.ewm_feature_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSysDto {
    @NotNull
    Long id;
    @NotNull
    Long userId;
    @NotNull
    String state;

}

