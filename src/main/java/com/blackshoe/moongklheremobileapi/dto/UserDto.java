package com.blackshoe.moongklheremobileapi.dto;

import lombok.*;

import java.time.LocalDateTime;

public class UserDto {

    @Data
    public static class SignInRequestDto {
        private String email;
        private String password;
        private String nickname;
        private String phoneNumber;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SignInResponseDto{
        private String userId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdatePasswordRequestDto{
        private String email;
        private String newPassword;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdatePasswordResponseDto{
        private String userId;
        private LocalDateTime updatedAt;
    }
}
