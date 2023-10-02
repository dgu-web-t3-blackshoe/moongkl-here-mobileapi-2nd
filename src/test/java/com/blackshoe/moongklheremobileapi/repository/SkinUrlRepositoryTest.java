package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SkinUrlRepositoryTest {

    @Autowired
    private SkinUrlRepository skinUrlRepository;

    @Test
    public void SkinUrlRepositoryIsNotNull() {
        assertThat(skinUrlRepository).isNotNull();
    }

    @Test
    public void SkinUrlSave() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("s3Url")
                .cloudfrontUrl("cloudfrontUrl")
                .build();

        //when
        final SkinUrl savedSkinUrl = skinUrlRepository.save(skinUrl);

        //then
        assertThat(savedSkinUrl).isNotNull();
        assertThat(savedSkinUrl.getId()).isNotNull();
        assertThat(savedSkinUrl.getS3Url()).isEqualTo("s3Url");
        assertThat(savedSkinUrl.getCloudfrontUrl()).isEqualTo("cloudfrontUrl");
    }

    @Test
    public void SkinUrlFindById() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("s3Url")
                .cloudfrontUrl("cloudfrontUrl")
                .build();

        final SkinUrl savedSkinUrl = skinUrlRepository.save(skinUrl);

        //when
        final SkinUrl findSkinUrl = skinUrlRepository.findById(savedSkinUrl.getId()).orElse(null);

        //then
        assertThat(findSkinUrl).isNotNull();
        assertThat(findSkinUrl.getId()).isNotNull();
        assertThat(findSkinUrl.getS3Url()).isEqualTo("s3Url");
        assertThat(findSkinUrl.getCloudfrontUrl()).isEqualTo("cloudfrontUrl");
    }
}
