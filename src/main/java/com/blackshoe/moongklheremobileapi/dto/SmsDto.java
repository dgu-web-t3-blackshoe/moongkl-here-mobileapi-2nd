package com.blackshoe.moongklheremobileapi.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class SmsDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class MessageDto {
        String to;
        String content;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Builder
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
    @Setter
    @Getter
    @Builder
    public static class SmsResponseDto {
        String requestId;
        LocalDateTime requestTime;
        String statusCode;
        String statusName;
    }
}
