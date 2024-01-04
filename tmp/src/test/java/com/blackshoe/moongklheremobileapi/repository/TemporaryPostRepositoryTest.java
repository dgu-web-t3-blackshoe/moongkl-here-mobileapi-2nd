package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.TemporaryPostDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TemporaryPostRepositoryTest {

    @Autowired
    private TemporaryPostRepository temporaryPostRepository;

    @Autowired
    private UserRepository userRepository;

    private Logger log = LoggerFactory.getLogger(TemporaryPostRepositoryTest.class);


    @Test
    public void assert_isNotNull() {
        assertThat(temporaryPostRepository).isNotNull();
    }

    @Test
    public void save_returns_isNotNull() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final User savedUser = userRepository.save(user);

        final TemporaryPost temporaryPost = TemporaryPost.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .build();

        temporaryPost.setUser(savedUser);

        //when
        final TemporaryPost savedTemporaryPost = temporaryPostRepository.save(temporaryPost);

        //then
        assertThat(savedTemporaryPost).isNotNull();
        assertThat(savedTemporaryPost.getId()).isNotNull();
        assertThat(savedTemporaryPost.getSkinUrl()).isEqualTo(skinUrl);
        assertThat(savedTemporaryPost.getStoryUrl()).isEqualTo(storyUrl);
        assertThat(savedTemporaryPost.getSkinTime()).isEqualTo(skinTime);
        assertThat(savedTemporaryPost.getSkinLocation()).isEqualTo(skinLocation);
        assertThat(savedTemporaryPost.getUser()).isEqualTo(user);
    }

    @Test
    public void findById_returns_savedTemporaryPost() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final User savedUser = userRepository.save(user);

        final TemporaryPost temporaryPost = TemporaryPost.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .build();

        temporaryPost.setUser(savedUser);

        final TemporaryPost savedTemporaryPost = temporaryPostRepository.save(temporaryPost);

        //when
        final TemporaryPost foundTemporaryPost = temporaryPostRepository.findById(savedTemporaryPost.getId()).orElse(null);

        //then
        assertThat(foundTemporaryPost).isNotNull();
        assertThat(foundTemporaryPost.getId()).isNotNull();
        assertThat(foundTemporaryPost.getSkinUrl()).isEqualTo(skinUrl);
        assertThat(foundTemporaryPost.getStoryUrl()).isEqualTo(storyUrl);
        assertThat(foundTemporaryPost.getSkinTime()).isEqualTo(skinTime);
        assertThat(foundTemporaryPost.getSkinLocation()).isEqualTo(skinLocation);
        assertThat(foundTemporaryPost.getUser()).isEqualTo(user);
    }
}
