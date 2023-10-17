package com.blackshoe.moongklheremobileapi.dto;

import com.blackshoe.moongklheremobileapi.entity.BackgroundImgUrl;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDto {
    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class SignInRequestDto {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String nickname;
        @NotBlank
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
        @NotBlank
        private String email;
        @NotBlank
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
        @NotBlank
        private String email;
        @NotBlank
        private String newPassword;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdatePasswordResponseDto{
        private UUID userId;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdateProfileRequestDto{
        private String nickname;
        private String statusMessage;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdateProfileDto{
        private UUID userId;
        private ProfileImgUrlDto profileImgUrlDto;
        private BackgroundImgUrlDto backgroundImgUrlDto;
        private String nickname;
        private String statusMessage;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdateProfileResponseDto{
        private UUID userId;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UserProfileInfoResponseDto{
        private UUID userId;
        private String nickname;
        private String statusMessage;
        private int likeCount;
        private int favoriteCount;
        private int postCount;
        private ProfileImgUrlDto profileImgUrlDto;
        private BackgroundImgUrlDto backgroundImgUrlDto;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UserBasicProfileInfoResponseDto{
        private UUID userId;
        private String nickname;
        private int postCount;
        private ProfileImgUrlDto profileImgUrlDto;
    }
    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UserMyProfileInfoResponseDto{
        private UUID userId;
        private String nickname;
        private String statusMessage;
        private BackgroundImgUrlDto backgroundImgUrlDto;
        private ProfileImgUrlDto profileImgUrlDto;
    }
}










