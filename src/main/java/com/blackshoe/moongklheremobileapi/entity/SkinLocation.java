package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "skin_locations")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class SkinLocation {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;


    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Builder
    public SkinLocation(UUID id, Double latitude, Double longitude, String country, String state, String city) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.state = state;
        this.city = city;
    }
}
