package com.blackshoe.moongklheremobileapi.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocationType {
    CURRENT("current"),
    DOMESTIC("domestic"),
    ABROAD("abroad"),
    DEFAULT("default");

    private final String locationType;
}
