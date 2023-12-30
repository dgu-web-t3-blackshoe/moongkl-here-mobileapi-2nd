package com.blackshoe.moongklheremobileapi.service;

public interface GeoCodingService {

    String getAddressFromCoordinate(Double latitude, Double longitude);

    String getCoordinateFromAddress(String address);

    String getAutocompleteResult(String input);
}
