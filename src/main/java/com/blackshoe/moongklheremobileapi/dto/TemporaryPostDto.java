package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import org.joda.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class TemporaryPostDto {
    private UUID postId;
    private UUID userId;
    private String skin;
    private String story;
    private SkinLocationDto location;
    private SkinTimeDto time;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class TemporaryPostCreateRequest {
        @NotNull(message = "location is required")
        private @Valid SkinLocationDto location;
        @NotNull(message = "time is required")
        private @Valid SkinTimeDto time;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class TemporaryPostCreateResponse {
        private String postId;
        private String createdAt;
    }
}
