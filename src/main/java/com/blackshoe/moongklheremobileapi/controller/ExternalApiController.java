package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.service.GeoCodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core/external-api")
@RequiredArgsConstructor
public class ExternalApiController {

    private final GeoCodingService geoCodingService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reverse-geocoding")
    public ResponseEntity<String> coordinateToAddress(@RequestParam Double latitude, @RequestParam Double longitude) {

        final String addrFromCoordinate = geoCodingService.getAddressFromCoordinate(latitude, longitude);

        return ResponseEntity.status(HttpStatus.OK).body(addrFromCoordinate);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/geocoding")
    public ResponseEntity<String> AddressToCoordinate(@RequestParam String address) {

        final String coordinateFromAddress = geoCodingService.getCoordinateFromAddress(address);

        return ResponseEntity.status(HttpStatus.OK).body(coordinateFromAddress);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/place-autocomplete")
    public ResponseEntity<String> autocomplete(@RequestParam String input) {

        final String autoCompleteResult = geoCodingService.getAutocompleteResult(input);

        return ResponseEntity.status(HttpStatus.OK).body(autoCompleteResult);
    }
}
