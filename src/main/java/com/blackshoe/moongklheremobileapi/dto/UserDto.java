package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDto {
    @Getter @Setter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class SignInRequestDto {
        private String email;
        private String password;
        private String nickname;
        private String phoneNumber;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class SignInResponseDto{
        private UUID userId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class LoginRequestDto{
        private String email;
        private String password;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class LoginResponseDto{
        private UUID userId;
        private LocalDateTime createdAt;
        private String accessToken;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdatePasswordRequestDto{
        private String email;
        private String newPassword;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdatePasswordResponseDto{
        private UUID userId;
        private LocalDateTime updatedAt;
    }
}
