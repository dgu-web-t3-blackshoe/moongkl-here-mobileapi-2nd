package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorResult {
    EMPTY_SKIN(HttpStatus.BAD_REQUEST, "스킨 파일이 누락되었습니다."),
    SKIN_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스킨 업로드에 실패했습니다."),
    INVALID_SKIN_SIZE(HttpStatus.BAD_REQUEST, "스킨 파일의 크기가 유효하지 않습니다."),
    INVALID_SKIN_TYPE(HttpStatus.BAD_REQUEST, "스킨 파일의 확장자가 유효하지 않습니다."),
    EMPTY_STORY(HttpStatus.BAD_REQUEST, "스토리 파일이 누락되었습니다."),
    STORY_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스토리 업로드에 실패했습니다."),
    INVALID_STORY_SIZE(HttpStatus.BAD_REQUEST, "스토리 파일의 크기가 유효하지 않습니다."),
    INVALID_STORY_TYPE(HttpStatus.BAD_REQUEST, "스토리 파일의 확장자가 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
