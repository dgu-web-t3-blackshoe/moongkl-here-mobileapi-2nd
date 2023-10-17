package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
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
    private long likeCount;
    private long favoriteCount;
    private long commentCount;
    private long viewCount;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostCreateRequest {
        @NotNull(message = "location is required")
        private @Valid SkinLocationDto location;
        @NotNull(message = "time is required")
        private @Valid SkinTimeDto time;
        @NotNull(message = "is_public is required")
        @Pattern(regexp = "^(true|false)$", message = "is_public must be true or false")
        private String isPublic;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostCreateResponse {
        private String postId;
        private String createdAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostUpdateResponse {
        private String postId;
        private String updatedAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostIsPublicChangeRequest {
        @NotNull(message = "is_public is required")
        @Pattern(regexp = "^(true|false)$", message = "is_public must be true or false")
        private String isPublic;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostReadResponse {
        private UUID postId;
        private UUID userId;
        private String skin;
        private String story;
        private SkinLocationDto location;
        private SkinTimeDto time;
        private long likeCount;
        private long favoriteCount;
        private long commentCount;
        private long viewCount;
        private Boolean isPublic;
        private String createdAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostListReadResponse {
        private UUID postId;
        private UUID userId;
        private String skin;
        private String story;

        public PostListReadResponse(UUID postId, UUID userId, String skin, String story) {
            this.postId = postId;
            this.userId = userId;
            this.skin = skin;
            this.story = story;
        }
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostGroupByCityReadResponse {
        private String country;
        private String state;
        private String city;
        private long postCount;
        private String thumbnail;

        public PostGroupByCityReadResponse(String country, String state, String city, long postCount, String thumbnail) {
            this.country = country;
            this.state = state;
            this.city = city;
            this.postCount = postCount;
            this.thumbnail = thumbnail;
        }
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class IncreaseViewCountDto {
        private UUID postId;
        private Long viewCount;
        private UUID userId;
        private LocalDateTime lastViewedAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LikePostDto {
        private UUID postId;
        private Long likeCount;
        private UUID userId;
        private LocalDateTime likedAt;
        private LocalDateTime dislikedAt;
    }
}
