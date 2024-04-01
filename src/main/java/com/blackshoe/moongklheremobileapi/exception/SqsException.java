package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SqsException extends RuntimeException{
    private final SqsErrorResult sqsErrorResult;
}
