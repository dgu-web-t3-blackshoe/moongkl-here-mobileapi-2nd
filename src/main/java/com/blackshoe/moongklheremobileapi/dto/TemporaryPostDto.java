package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TemporaryPostDto {
    private UUID temporaryPostId;
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
        private String temporaryPostId;
        private String createdAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class TemporaryPostListReadResponse {
        private UUID temporaryPostId;
        private UUID userId;
        private String skin;
        private String story;

        public TemporaryPostListReadResponse(UUID temporaryPostId, UUID userId, String skin, String story) {
            this.temporaryPostId = temporaryPostId;
            this.userId = userId;
            this.skin = skin;
            this.story = story;
        }
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DeleteResponse {
        private String temporaryPostId;
        private String deletedAt;
    }
}
