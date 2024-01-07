package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException {
    private final PostErrorResult postErrorResult;
}
