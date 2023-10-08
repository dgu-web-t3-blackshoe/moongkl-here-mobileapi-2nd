package com.blackshoe.moongklheremobileapi.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class SkinTimeDto {
    @NotNull(message = "year is required")
    private int year;
    @NotNull(message = "month is required")
    private int month;
    @NotNull(message = "day is required")
    private int day;
    @NotNull(message = "hour is required")
    private int hour;
    @NotNull(message = "minute is required")
    private int minute;
}
