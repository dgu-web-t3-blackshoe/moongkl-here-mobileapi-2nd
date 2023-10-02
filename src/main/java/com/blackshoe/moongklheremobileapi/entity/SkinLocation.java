package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "skin_locations")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class SkinLocation {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private Double longitude;

    private Double latitude;

    private String country;

    private String state;

    private String city;

    @Builder
    public SkinLocation(UUID id, Double longitude, Double latitude, String country, String state, String city) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.state = state;
        this.city = city;
    }
}
