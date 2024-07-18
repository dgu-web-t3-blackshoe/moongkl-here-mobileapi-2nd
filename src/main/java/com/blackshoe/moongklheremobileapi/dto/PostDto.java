package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

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
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostUpdateResponse {
        private String postId;
        private LocalDateTime updatedAt;
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
        private LocalDateTime createdAt;
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
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DislikePostDto {
        private UUID postId;
        private Long likeCount;
        private UUID userId;
        private LocalDateTime deletedAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FavoritePostDto {
        private UUID postId;
        private Long favoriteCount;
        private UUID userId;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeleteFavoritePostDto {
        private UUID postId;
        private Long favoriteCount;
        private UUID userId;
        private LocalDateTime deletedAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SaveTemporaryPostRequest {
        @NotNull(message = "temporary_post_id is required")
        private UUID temporaryPostId;
        @NotNull(message = "is_public is required")
        @Pattern(regexp = "^(true|false)$", message = "is_public must be true or false")
        private String isPublic;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DeletePostResponse {
        private UUID postId;
        private LocalDateTime deletedAt;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DidUserLikedPostResponse {
        private Boolean isTrue;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DidUserFavoritePostResponse {
        private Boolean isTrue;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PostWithDateListReadResponse {
        private UUID postId;
        private UUID userId;
        private String skin;
        private String story;
        private int year;
        private int month;
        private int day;

        public PostWithDateListReadResponse(UUID postId, UUID userId, String skin, String story, int year, int month, int day) {
            this.postId = postId;
            this.userId = userId;
            this.skin = skin;
            this.story = story;
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EnterpriseStoryReadResponse {
        private UUID storyId;
        private UUID enterpriseId;
        private String cloudfrontUrl;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EnterpriseSearchReadResponse{
        private UUID enterpriseId;
        private String enterpriseName;
        List<EnterpriseStoryList> enterpriseStoryList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EnterpriseStoryList{
        private UUID storyId;
        private String cloudfrontUrl;
        private LocalDateTime createdAt;
    }
}
