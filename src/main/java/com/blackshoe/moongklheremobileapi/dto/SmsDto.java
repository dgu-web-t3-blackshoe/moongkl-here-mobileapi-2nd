package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

public class SmsDto {

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ValidationRequestDto{
        @NotBlank
        @Pattern(regexp = "^01[016-9]\\d{8}$", message = "유효하지 않은 전화번호입니다. 재시도해주세요.")
        String phoneNumber;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class VerificationRequestDto{
        @NotBlank
        @Pattern(regexp = "^01[016-9]\\d{8}$", message = "유효하지 않은 전화번호입니다. 재시도해주세요.")
        String phoneNumber;
        @NotBlank
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
