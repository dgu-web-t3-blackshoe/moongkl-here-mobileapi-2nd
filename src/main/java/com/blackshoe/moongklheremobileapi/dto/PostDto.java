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
public class PostDto {
    private UUID postId;
    private UUID userId;
    private String skin;
    private String story;
    private SkinLocationDto location;
    private SkinTimeDto time;
    private int likeCount;
    private int favoriteCount;
    private int commentCount;
    private int viewCount;
    private Boolean isPublic;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostCreateRequest {
        @NotNull(message = "location is required")
        private @Valid SkinLocationDto location;
        @NotNull(message = "time is required")
        private @Valid SkinTimeDto time;
        @NotNull(message = "is_public is required")
        private Boolean isPublic;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostCreateResponse {
        private String postId;
        private String createdAt;
    }
}
