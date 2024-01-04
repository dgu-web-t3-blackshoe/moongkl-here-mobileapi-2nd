package com.blackshoe.moongklheremobileapi.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostAddressFilter {
    private String country;
    private String state;
    private String city;
}
