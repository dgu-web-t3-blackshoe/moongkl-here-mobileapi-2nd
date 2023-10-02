package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StoryUrlRepositoryTest {

    @Autowired
    private StoryUrlRepository storyUrlRepository;

    @Test
    public void StoryUrlRepositoryIsNotNull() {
        assertThat(storyUrlRepository).isNotNull();
    }

    @Test
    public void StoryUrlSave() {
        //given
        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("s3Url")
                .cloudfrontUrl("cloudfrontUrl")
                .build();

        //when
        final StoryUrl savedStoryUrl = storyUrlRepository.save(storyUrl);

        //then
        assertThat(savedStoryUrl).isNotNull();
        assertThat(savedStoryUrl.getS3Url()).isEqualTo("s3Url");
        assertThat(savedStoryUrl.getCloudfrontUrl()).isEqualTo("cloudfrontUrl");
    }

    @Test
    public void StoryUrlFindById() {
        //given
        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("s3Url")
                .cloudfrontUrl("cloudfrontUrl")
                .build();

        final StoryUrl savedStoryUrl = storyUrlRepository.save(storyUrl);

        //when
        final StoryUrl foundStoryUrl = storyUrlRepository.findById(storyUrl.getId()).orElse(null);

        //then
        assertThat(foundStoryUrl).isNotNull();
        assertThat(foundStoryUrl.getId()).isNotNull();
        assertThat(foundStoryUrl.getS3Url()).isEqualTo("s3Url");
        assertThat(foundStoryUrl.getCloudfrontUrl()).isEqualTo("cloudfrontUrl");
    }
}
