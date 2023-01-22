package ru.practicum.explore_with_me.ewm_feature_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {

    @NotNull
    Long userId;
    @NotNull
    Long eventId;
    @NotNull
    String createdDate;
    @NotBlank
    String description;
}

