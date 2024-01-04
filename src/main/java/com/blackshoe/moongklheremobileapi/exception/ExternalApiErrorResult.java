package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExternalApiErrorResult {

    GEO_CODING_SERVICE_4XX_ERROR(HttpStatus.BAD_REQUEST, "지오코딩 서비스 4xx 에러"),
    GEO_CODING_SERVICE_5XX_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "지오코딩 서비스 5xx 에러");

    private final HttpStatus httpStatus;
    private final String message;
}
