package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.SkinLocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SkinLocationRepositoryTest {

    @Autowired
    private SkinLocationRepository skinLocationRepository;

    @Test
    public void SkinLocationRepositoryIsNotNull() {
        assertThat(skinLocationRepository).isNotNull();
    }

    @Test
    public void SkinLocationSave() {
        //given
        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(10.0)
                .longitude(10.0)
                .country("country")
                .state("state")
                .city("city")
                .build();

        //when
        final SkinLocation savedSkinLocation = skinLocationRepository.save(skinLocation);

        //then
        assertThat(savedSkinLocation).isNotNull();
        assertThat(savedSkinLocation.getId()).isNotNull();
        assertThat(savedSkinLocation.getLatitude()).isEqualTo(10.0);
        assertThat(savedSkinLocation.getLongitude()).isEqualTo(10.0);
        assertThat(savedSkinLocation.getCountry()).isEqualTo("country");
        assertThat(savedSkinLocation.getState()).isEqualTo("state");
        assertThat(savedSkinLocation.getCity()).isEqualTo("city");
    }

    @Test
    public void SkinLocationFindById() {
        //given
        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(10.0)
                .longitude(10.0)
                .country("country")
                .state("state")
                .city("city")
                .build();

        final SkinLocation savedSkinLocation = skinLocationRepository.save(skinLocation);

        //when
        final SkinLocation foundSkinLocation = skinLocationRepository.findById(savedSkinLocation.getId()).orElse(null);

        //then
        assertThat(foundSkinLocation).isNotNull();
        assertThat(foundSkinLocation.getId()).isNotNull();
        assertThat(foundSkinLocation.getLatitude()).isEqualTo(10.0);
        assertThat(foundSkinLocation.getLongitude()).isEqualTo(10.0);
        assertThat(foundSkinLocation.getCountry()).isEqualTo("country");
        assertThat(foundSkinLocation.getState()).isEqualTo("state");
        assertThat(foundSkinLocation.getCity()).isEqualTo("city");
    }
}
