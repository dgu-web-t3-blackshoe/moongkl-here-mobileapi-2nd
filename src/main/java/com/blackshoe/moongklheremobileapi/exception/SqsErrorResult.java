package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum SqsErrorResult {
    INVALID_SENDER(HttpStatus.BAD_REQUEST, "유효하지 않은 발신자입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
