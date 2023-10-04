package com.blackshoe.moongklheremobileapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkinTimeDto {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
}
