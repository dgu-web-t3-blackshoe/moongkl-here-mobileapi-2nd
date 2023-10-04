package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.SkinLocationDto;
import com.blackshoe.moongklheremobileapi.dto.SkinTimeDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.blackshoe.moongklheremobileapi.repository.SkinLocationRepository;
import com.blackshoe.moongklheremobileapi.repository.SkinTimeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SkinLocationRepository skinLocationRepository;

    @Mock
    private SkinTimeRepository skinTimeRepository;

    private final Logger log = LoggerFactory.getLogger(PostServiceTest.class);

    @Test
    public void 포스트_등록() {
        // given
        final UUID skinUrlId = UUID.randomUUID();

        final SkinUrl skinUrl = SkinUrl.builder()
                .id(skinUrlId)
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final UUID storyUrlId = UUID.randomUUID();

        final StoryUrl storyUrl = StoryUrl.builder()
                .id(storyUrlId)
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final UUID userId = UUID.randomUUID();

        final User user = User.builder()
                .id(userId)
                .nickname("test")
                .email("test")
                .password("test")
                .phoneNumber("test")
                .build();

        final SkinLocationDto skinLocationDto = SkinLocationDto.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final SkinTimeDto skinTimeDto = SkinTimeDto.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final PostDto.PostCreateRequest postCreateRequest = PostDto.PostCreateRequest.builder()
                .location(skinLocationDto)
                .time(skinTimeDto)
                .isPublic(true)
                .build();

        // when
        final Post post = postService.createPost(user, skinUrl, storyUrl, postCreateRequest);

        // then
        assertThat(post.getUser().getId()).isEqualTo(userId);
        assertThat(post.getSkinUrl().getId()).isEqualTo(skinUrlId);
        assertThat(post.getStoryUrl().getId()).isEqualTo(storyUrlId);
        assertThat(post.getSkinLocation().getLatitude()).isEqualTo(skinLocationDto.getLatitude());
        assertThat(post.getSkinLocation().getLongitude()).isEqualTo(skinLocationDto.getLongitude());
        assertThat(post.getSkinLocation().getCountry()).isEqualTo(skinLocationDto.getCountry());
        assertThat(post.getSkinLocation().getState()).isEqualTo(skinLocationDto.getState());
        assertThat(post.getSkinLocation().getCity()).isEqualTo(skinLocationDto.getCity());
        assertThat(post.getSkinTime().getYear()).isEqualTo(skinTimeDto.getYear());
        assertThat(post.getSkinTime().getMonth()).isEqualTo(skinTimeDto.getMonth());
        assertThat(post.getSkinTime().getDay()).isEqualTo(skinTimeDto.getDay());
        assertThat(post.getSkinTime().getHour()).isEqualTo(skinTimeDto.getHour());
        assertThat(post.getSkinTime().getMinute()).isEqualTo(skinTimeDto.getMinute());
        assertThat(post.isPublic()).isEqualTo(postCreateRequest.getIsPublic());
    }
}
