package com.blackshoe.moongklheremobileapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MailDto {

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MailRequestDto {
        private String email;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MailSendDto{
        private String email;
        private String title;
        private String content;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MailVerifyDto{
        private String email;
        private String verificationCode;
    }
}
