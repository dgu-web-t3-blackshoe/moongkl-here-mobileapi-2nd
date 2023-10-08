package com.blackshoe.moongklheremobileapi.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class SkinLocationDto {
    @NotNull(message = "longitude is required")
    private Double longitude;
    @NotNull(message = "latitude is required")
    private Double latitude;
    @NotBlank(message = "country is required")
    private String country;
    @NotBlank(message = "state is required")
    private String state;
    @NotBlank(message = "city is required")
    private String city;
}
