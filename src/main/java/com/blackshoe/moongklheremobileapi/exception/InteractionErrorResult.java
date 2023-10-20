package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.HTTP;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InteractionErrorResult {

    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "즐겨찾기가 등록되지 않은 게시글입니다."),
    USER_ALREADY_LIKED_POST(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요가 등록되지 않은 게시글입니다."),
    FAVORITE_USER_NOT_MATCH(HttpStatus.FORBIDDEN, "즐겨찾기를 등록한 사용자가 아닙니다."),
    LIKE_USER_NOT_MATCH(HttpStatus.FORBIDDEN, "좋아요를 등록한 사용자가 아닙니다."),
    FAVORITE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 즐겨찾기한 게시글입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
