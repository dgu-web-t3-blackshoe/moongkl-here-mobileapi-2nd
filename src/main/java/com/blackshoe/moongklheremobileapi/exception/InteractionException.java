package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InteractionException extends RuntimeException {
    private final InteractionErrorResult interactionErrorResult;
}
