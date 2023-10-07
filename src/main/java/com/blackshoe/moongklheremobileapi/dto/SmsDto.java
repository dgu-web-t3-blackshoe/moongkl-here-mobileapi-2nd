package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class SmsDto {

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ValidationRequestDto{
        String phoneNumber;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class VerificationRequestDto{
        String phoneNumber;
        String verificationCode;
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class MessageDto {
        String to;
        String content;
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class SmsRequestDto {
        String type;
        String contentType;
        String countryCode;
        String from;
        String content;
        List<MessageDto> messages;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SmsResponseDto {
        String requestId;
        LocalDateTime requestTime;
        String statusCode;
        String statusName;
    }
}
