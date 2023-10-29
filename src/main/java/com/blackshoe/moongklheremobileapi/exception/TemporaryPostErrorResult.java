package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TemporaryPostErrorResult {
    TEMPORARY_POST_NOT_FOUND(HttpStatus.NOT_FOUND,"임시보관한 게시글을 찾을 수 없습니다."),
    USER_NOT_MATCH(HttpStatus.FORBIDDEN, "임시보관 게시글 작성자가 아닙니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
