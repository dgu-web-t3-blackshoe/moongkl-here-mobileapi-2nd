package com.blackshoe.moongklheremobileapi.vo;

import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
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

    public static LocationType verifyAndConvertStringToLocationType(String location) {
        if (location.equals(CURRENT.getLocationType())) {
            return LocationType.CURRENT;
        }
        if (location.equals(DOMESTIC.getLocationType())) {
            return LocationType.DOMESTIC;
        }
        if (location.equals(ABROAD.getLocationType())) {
            return LocationType.ABROAD;
        }
        if (location.equals(DEFAULT.getLocationType())) {
            return LocationType.DEFAULT;
        }
        throw new PostException(PostErrorResult.INVALID_LOCATION_TYPE);
    }
}
