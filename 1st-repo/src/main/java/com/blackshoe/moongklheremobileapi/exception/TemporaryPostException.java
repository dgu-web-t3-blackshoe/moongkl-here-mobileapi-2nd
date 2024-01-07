package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TemporaryPostException extends RuntimeException {
    private final TemporaryPostErrorResult temporaryPostErrorResult;
}
