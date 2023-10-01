package com.blackshoe.moongklheremobileapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class UserDto {

    @Data
    public static class SignInRequestDto {
        private String email;
        private String password;
        private String nickname;
        private String phoneNumber;
    }
}
