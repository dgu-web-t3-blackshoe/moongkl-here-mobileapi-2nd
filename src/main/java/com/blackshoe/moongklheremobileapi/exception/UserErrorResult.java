package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult {
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."), //409
    REQUIRED_VALUE(HttpStatus.BAD_REQUEST, "필수 값이 누락되었습니다."), //400
    INVALID_PHONE_NUMBER(HttpStatus.UNPROCESSABLE_ENTITY, "유효하지 않은 전화번호입니다. 재시도해주세요."), //422
    INVALID_PASSWORD(HttpStatus.UNPROCESSABLE_ENTITY, "유효하지 않은 비밀번호(8자리 이상 20자리 이하이며 특수 문자 최소 하나 포함)입니다. 재시도해주세요."), //422
    INVALID_EMAIL(HttpStatus.UNPROCESSABLE_ENTITY, "유효하지 않은 이메일입니다. 재시도해주세요."), //422
    INVALID_NICKNAME(HttpStatus.UNPROCESSABLE_ENTITY, "유효하지 않은 닉네임(8자리 이하, 특수문자X)입니다. 재시도해주세요."), //422
    FAILED_VALIDATING_CODE(HttpStatus.BAD_REQUEST, "인증코드가 일치하지 않습니다. 인증 코드를 재요청해 주세요."), //400
    UNVERIFIED_PHONE_NUMBER(HttpStatus.UNPROCESSABLE_ENTITY, "인증되지 않은 전화번호입니다. 재시도해주세요."), //422
    UNVERIFIED_EMAIL(HttpStatus.UNPROCESSABLE_ENTITY, "인증되지 않은 이메일입니다. 재시도해주세요."), //422
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."), //404
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT입니다."), //401
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT입니다."), //401
    NOT_FOUND_JWT(HttpStatus.UNAUTHORIZED, "존재하지 않는 JWT입니다."), //401
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
