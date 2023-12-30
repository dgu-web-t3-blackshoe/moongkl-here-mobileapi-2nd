package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExternalApiException extends RuntimeException {

    private final ExternalApiErrorResult externalApiErrorResult;
}
