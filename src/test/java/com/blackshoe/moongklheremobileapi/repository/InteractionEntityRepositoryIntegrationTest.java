package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class InteractionEntityRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ViewRepository viewRepository;

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

    final UUID userId = UUID.randomUUID();

    final User user = User.builder()
            .id(userId)
            .email("test")
            .password("test")
            .nickname("test")
            .phoneNumber("test")
            .build();

    final UUID postId = UUID.randomUUID();

    final Post post = Post.builder()
            .id(postId)
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

    @Test
    public void findByPostAndUser_returns_isNotNull() {

        //given
        final User savedUser = userRepository.save(user);
        final Post savedPost = postRepository.save(post);

        final View view = View.builder()
                .post(savedPost)
                .user(savedUser)
                .build();

        final View savedView = viewRepository.save(view);

        //when
        final View foundView = viewRepository.findByPostAndUser(savedPost, savedUser).orElse(null);

        //then
        assertThat(foundView).isNotNull();
        assertThat(foundView.getPost()).isNotNull();
        assertThat(foundView.getUser()).isNotNull();
    }
}
