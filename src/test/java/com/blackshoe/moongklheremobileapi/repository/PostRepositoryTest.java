package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    private Logger log = LoggerFactory.getLogger(PostRepositoryTest.class);

    @Test
    public void assert_isNotNull() {
        assertThat(postRepository).isNotNull();
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

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .user(user)
                .likeCount(10)
                .favoriteCount(100)
                .viewCount(20)
                .isPublic(true)
                .build();

        //when
        final Post savedPost = postRepository.save(post);

        //then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();
        log.info("savedPost.getId() = {}", savedPost.getId());
        assertThat(savedPost.getSkinUrl()).isEqualTo(skinUrl);
        log.info("skinUrl.getId() = {}", savedPost.getSkinUrl().getId());
        assertThat(savedPost.getStoryUrl()).isEqualTo(storyUrl);
        log.info("storyUrl.getId() = {}", savedPost.getStoryUrl().getId());
        assertThat(savedPost.getSkinTime()).isEqualTo(skinTime);
        log.info("skinTime.getId() = {}", savedPost.getSkinTime().getId());
        assertThat(savedPost.getSkinLocation()).isEqualTo(skinLocation);
        log.info("skinLocation.getId() = {}", savedPost.getSkinLocation().getId());
        assertThat(savedPost.getUser()).isEqualTo(user);
        log.info("user.getId() = {}", savedPost.getUser().getId());
        assertThat(savedPost.getLikeCount()).isEqualTo(10);
        assertThat(savedPost.getFavoriteCount()).isEqualTo(100);
        assertThat(savedPost.getViewCount()).isEqualTo(20);
        assertThat(savedPost.isPublic()).isEqualTo(true);
        assertThat(savedPost.getCreatedAt()).isNotNull();
        log.info("savedPost.getCreatedAt() = {}", savedPost.getCreatedAt());
    }

    @Test
    public void findById_returns_savedPost() {
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

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .user(user)
                .likeCount(10)
                .favoriteCount(100)
                .viewCount(20)
                .isPublic(true)
                .build();

        final Post savedPost = postRepository.save(post);

        //when
        Post foundPost = postRepository.findById(post.getId()).orElse(null);

        //then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getId()).isEqualTo(post.getId());
        assertThat(foundPost.getSkinUrl()).isEqualTo(skinUrl);
        assertThat(foundPost.getStoryUrl()).isEqualTo(storyUrl);
        assertThat(foundPost.getSkinTime()).isEqualTo(skinTime);
        assertThat(foundPost.getSkinLocation()).isEqualTo(skinLocation);
        assertThat(foundPost.getUser()).isEqualTo(user);
        assertThat(foundPost.getLikeCount()).isEqualTo(10);
        assertThat(foundPost.getFavoriteCount()).isEqualTo(100);
        assertThat(foundPost.getViewCount()).isEqualTo(20);
        assertThat(foundPost.isPublic()).isEqualTo(true);
        assertThat(foundPost.getCreatedAt()).isNotNull();
    }
}
