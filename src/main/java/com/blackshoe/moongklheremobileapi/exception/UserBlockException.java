package com.blackshoe.moongklheremobileapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter @RequiredArgsConstructor
public class UserBlockException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final String message;
    public UserBlockException(String message) {
        super(message);
        this.httpStatus = HttpStatus.FORBIDDEN;
        this.message = message;
    }
}
