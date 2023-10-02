package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.SkinTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SkinTimeRepositoryTest {

    @Autowired
    private SkinTimeRepository skinTimeRepository;

    @Test
    public void SkinTimeRepositoryIsNotNull() {
        assertThat(skinTimeRepository).isNotNull();
    }

    @Test
    public void SkinTimeSave() {
        //given
        final SkinTime skinTime = SkinTime.builder()
                .year(2020)
                .month(10)
                .day(10)
                .hour(10)
                .minute(10)
                .build();

        //when
        final SkinTime savedSkinTime = skinTimeRepository.save(skinTime);

        //then
        assertThat(savedSkinTime).isNotNull();
        assertThat(savedSkinTime.getId()).isNotNull();
        assertThat(savedSkinTime.getYear()).isEqualTo(2020);
        assertThat(savedSkinTime.getMonth()).isEqualTo(10);
        assertThat(savedSkinTime.getDay()).isEqualTo(10);
        assertThat(savedSkinTime.getHour()).isEqualTo(10);
        assertThat(savedSkinTime.getMinute()).isEqualTo(10);
    }

    @Test
    public void SkinTimeFindById() {
        //given
        final SkinTime skinTime = SkinTime.builder()
                .year(2020)
                .month(10)
                .day(10)
                .hour(10)
                .minute(10)
                .build();

        final SkinTime savedSkinTime = skinTimeRepository.save(skinTime);

        //when
        final SkinTime foundSkinTime = skinTimeRepository.findById(savedSkinTime.getId()).orElse(null);

        //then
        assertThat(foundSkinTime).isNotNull();
        assertThat(foundSkinTime.getId()).isNotNull();
        assertThat(foundSkinTime.getYear()).isEqualTo(2020);
        assertThat(foundSkinTime.getMonth()).isEqualTo(10);
        assertThat(foundSkinTime.getDay()).isEqualTo(10);
        assertThat(foundSkinTime.getHour()).isEqualTo(10);
        assertThat(foundSkinTime.getMinute()).isEqualTo(10);
    }
}
