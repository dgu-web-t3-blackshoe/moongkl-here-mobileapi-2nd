package com.blackshoe.moongklheremobileapi.dto;

import com.blackshoe.moongklheremobileapi.entity.BackgroundImgUrl;
import com.blackshoe.moongklheremobileapi.entity.ProfileImgUrl;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDto {
    @Getter @Setter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class SignUpRequestDto {

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일입니다. 재시도해주세요.")
        private String email;

        @NotBlank
        @Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>])[\\S]{8,}$", message = "유효하지 않은 비밀번호(8자리 이상, 특수 문자 최소 하나 포함)입니다. 재시도해주세요.")
        private String password;

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "유효하지 않은 닉네임(2자리 이상 10자리 사이이며 특수 문자 미포함)입니다. 재시도해주세요.")
        private String nickname;

        @NotBlank
        @Pattern(regexp = "^01[016-9]\\d{8}$", message = "유효하지 않은 전화번호입니다. 재시도해주세요.")
        private String phoneNumber;

        @NotBlank
        private String gender;

        @NotBlank
        private String country;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class SignUpResponseDto{
        private UUID userId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class LoginRequestDto{
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일입니다. 재시도해주세요.")
        private String email;
        @NotBlank
        @Pattern(regexp = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "유효하지 않은 비밀번호(8자리 이상 20자리 이하이며 특수 문자 최소 하나 포함)입니다. 재시도해주세요.")
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
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일입니다. 재시도해주세요.")
        private String email;

        @NotBlank
        @Pattern(regexp = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "유효하지 않은 비밀번호(8자리 이상 20자리 이하이며 특수 문자 최소 하나 포함)입니다. 재시도해주세요.")
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
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "유효하지 않은 닉네임(2자리 이상 10자리 사이이며 특수 문자 미포함)입니다. 재시도해주세요.")
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

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "유효하지 않은 닉네임(2자리 이상 10자리 사이이며 특수 문자 미포함)입니다. 재시도해주세요.")
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
        private ProfileImgUrlDto profileImgUrlDto;
        private BackgroundImgUrlDto backgroundImgUrlDto;
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

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdatePasswordInMyHereRequestDto{
        @NotBlank
        @Pattern(regexp = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "유효하지 않은 비밀번호(8자리 이상 20자리 이하이며 특수 문자 최소 하나 포함)입니다. 재시도해주세요.")
        private String currentPassword;
        @NotBlank
        @Pattern(regexp = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "유효하지 않은 비밀번호(8자리 이상 20자리 이하이며 특수 문자 최소 하나 포함)입니다. 재시도해주세요.")
        private String newPassword;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdatePhoneNumberResponseDto{
        private UUID userId;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class CheckDuplicatedEmailRequestDto{
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일입니다. 재시도해주세요.")
        private String email;
    }


    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdatePhoneNumberRequestDto{
        @NotBlank
        @Pattern(regexp = "^01[016-9]\\d{8}$", message = "유효하지 않은 전화번호입니다. 재시도해주세요.")
        private String phoneNumber;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class GetPhoneNumberResponseDto{
        private UUID userId;
        private String phoneNumber;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class GetEmailResponseDto{
        private UUID userId;
        private String email;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ValidatePasswordRequestDto{
        @NotBlank
        @Pattern(regexp = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "유효하지 않은 비밀번호(8자리 이상 20자리 이하이며 특수 문자 최소 하나 포함)입니다. 재시도해주세요.")
        private String password;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class SocialSignUpResponseDto{
        private UUID userId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdateProfileImgRequestDto{
        private UUID userId;
        private MultipartFile profileImg;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class UpdateProfileImgResponseDto{
        private UUID userId;
        private LocalDateTime updatedAt;
        private ProfileImgUrlDto profileImgUrlDto;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class GetUserIdRequestDto {
        private String email;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class GetUserIdResponseDto {
        private UUID userId;
    }
}










