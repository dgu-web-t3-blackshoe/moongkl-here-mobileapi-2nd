package com.blackshoe.moongklheremobileapi.exception;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResponseDto> handleMissingServletRequestPartException(MissingServletRequestPartException e) {

        log.error("MissingServletRequestPartException", e);

        final ResponseDto responseDto = ResponseDto.builder()
                .error(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDto> handleBindException(BindException e) {

        final String errors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error("BindException", errors);

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {

        log.error("MethodArgumentTypeMismatchException", e);

        final ResponseDto responseDto = ResponseDto.builder()
                .error(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ResponseDto> handlePostException(PostException e) {

        log.error("PostException", e);

        final PostErrorResult errorResult = e.getPostErrorResult();

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errorResult.getMessage())
                .build();

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }

    @ExceptionHandler(InteractionException.class)
    public ResponseEntity<ResponseDto> handleInteractionException(InteractionException e) {

        log.error("InteractionException", e);

        final InteractionErrorResult errorResult = e.getInteractionErrorResult();

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errorResult.getMessage())
                .build();

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }

    @ExceptionHandler(TemporaryPostException.class)
    public ResponseEntity<ResponseDto> handleTemporaryPostException(TemporaryPostException e) {

        log.error("TemporaryPostException", e);

        final TemporaryPostErrorResult errorResult = e.getTemporaryPostErrorResult();

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errorResult.getMessage())
                .build();

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ResponseDto> handleUserException(UserException e) {

        log.error("UserException", e);

        final UserErrorResult errorResult = e.getUserErrorResult();

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errorResult.getMessage())
                .build();

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ResponseDto> handleExternalApiException(ExternalApiException e) {

        log.error("ExternalApiException", e);

        final ExternalApiErrorResult errorResult = e.getExternalApiErrorResult();

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errorResult.getMessage())
                .build();

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }

    @ExceptionHandler(SqsException.class)
    public ResponseEntity<ResponseDto> handleSqsException(SqsException e) {

        log.error("SqsException", e);

        final SqsErrorResult errorResult = e.getSqsErrorResult();

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errorResult.getMessage())
                .build();

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception e) {

        log.error("Exception" + String.valueOf(e));

        if(e.getClass().getName().equals("org.springframework.security.access.AccessDeniedException")){
            final ResponseDto responseDto = ResponseDto.builder()
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }

        final ResponseDto responseDto = ResponseDto.builder()
                .error(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }


}
