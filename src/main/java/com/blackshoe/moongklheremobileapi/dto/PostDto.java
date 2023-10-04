package com.blackshoe.moongklheremobileapi.dto;

import com.blackshoe.moongklheremobileapi.entity.SkinLocation;
import lombok.Builder;
import lombok.Getter;

public class PostDto {
    @Getter
    @Builder
    public static class PostCreateRequest {
        private SkinLocationDto location;
        private SkinTimeDto time;
        private Boolean isPublic;
    }
}
