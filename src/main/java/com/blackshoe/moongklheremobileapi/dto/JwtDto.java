package com.blackshoe.moongklheremobileapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class JwtDto {

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class JwtRequestDto{
        UUID userId;
        String email;
    }

    @Getter
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class JwtResponseDto{
        UUID userId;
        String jwt;
    }
}
