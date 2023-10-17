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
    INVALID_STORY_TYPE(HttpStatus.BAD_REQUEST, "스토리 파일의 확장자가 유효하지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    INVALID_LOCATION_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 위치 타입입니다."),
    GET_POST_LIST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 목록을 가져오는데 실패했습니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "날짜 형식이 유효하지 않습니다."),
    INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 정렬 타입입니다."),
    USER_NOT_MATCH(HttpStatus.FORBIDDEN, "게시글 작성자가 아닙니다."),
    INVALID_PARAMETER_FOR_GET_POST_LIST(HttpStatus.BAD_REQUEST, "유효하지 않은 파라미터입니다."),
    USER_ALREADY_LIKED_POST(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요가 등록되지 않은 게시글입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
