package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
    private final UserErrorResult userErrorResult;
    //UserPartiallyCreatedException, UserNecessarySignInException
    public UserException(UserErrorResult userErrorResult, String message) {
        super(message);
        this.userErrorResult = userErrorResult;
    }
}

