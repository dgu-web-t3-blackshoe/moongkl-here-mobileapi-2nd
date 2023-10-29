package com.blackshoe.moongklheremobileapi.vo;

import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
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

    public static SortType verifyAndConvertStringToSortType(String sort) {
        if (sort.equals(LIKES.getSortType())) {
            return SortType.LIKES;
        }
        if (sort.equals(VIEWS.getSortType())) {
            return SortType.VIEWS;
        }
        if (sort.equals(DEFAULT.getSortType())) {
            return SortType.DEFAULT;
        }
        throw new PostException(PostErrorResult.INVALID_SORT_TYPE);
    }
}
