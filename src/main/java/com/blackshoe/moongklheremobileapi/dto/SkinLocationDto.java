package com.blackshoe.moongklheremobileapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkinLocationDto {
    private Double longitude;
    private Double latitude;
    private String country;
    private String state;
    private String city;
}
