package com.blackshoe.moongklheremobileapi.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    LIKES("likes"),
    VIEWS("views"),
    DEFAULT("default");

    private final String sortType;

    public static String getSortField(SortType sortType) {
        switch (sortType) {
            case LIKES:
                return "likeCount";
            case VIEWS:
                return "viewCount";
            default:
                return "createdAt";
        }
    }
}
