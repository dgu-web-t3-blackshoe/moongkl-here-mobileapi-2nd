package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import org.joda.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    public static class PostListGroupByCityReadResponse {
        private List<PostGroupByCityReadResponse> regions;

        public PostListGroupByCityReadResponse(List<PostGroupByCityReadResponse> regions) {
            this.regions = regions;
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
}
