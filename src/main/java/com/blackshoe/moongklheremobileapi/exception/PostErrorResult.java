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
    SKIN_URL_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "스킨을 찾을 수 없습니다."),
    STORY_URL_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "스토리를 찾을 수 없습니다."),
    SKIN_TIME_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "스킨 시간을 찾을 수 없습니다."),
    SKIN_LOCATION_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "스킨 위치를 찾을 수 없습니다."),
    INVALID_PARAMETER_VALUE_FOR_SAVE_TEMPORARY_POST(HttpStatus.BAD_REQUEST, "유효하지 않은 파라미터 값입니다."),
    SKIN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스킨 삭제에 실패했습니다."),
    STORY_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스토리 삭제에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
