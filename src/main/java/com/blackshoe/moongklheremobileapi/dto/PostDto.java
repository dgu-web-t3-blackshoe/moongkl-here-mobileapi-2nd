package com.blackshoe.moongklheremobileapi.dto;

import com.blackshoe.moongklheremobileapi.entity.SkinLocation;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.joda.time.LocalDateTime;

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
    public static class PostCreateRequest {
        private SkinLocationDto location;
        private SkinTimeDto time;
        private Boolean isPublic;
    }

    @Data
    @Builder
    public static class PostCreateResponse {
        private UUID postId;
        private LocalDateTime createdAt;
    }
}
