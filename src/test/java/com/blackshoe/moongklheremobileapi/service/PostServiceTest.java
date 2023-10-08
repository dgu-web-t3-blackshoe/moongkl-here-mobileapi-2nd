package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.config.ModelMapperConfig;
import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.blackshoe.moongklheremobileapi.repository.SkinLocationRepository;
import com.blackshoe.moongklheremobileapi.repository.SkinTimeRepository;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    private Logger log = LoggerFactory.getLogger(PostServiceTest.class);

    private final SkinUrlDto skinUrlDto = SkinUrlDto.builder()
            .s3Url("test")
            .cloudfrontUrl("test")
            .build();

    private final SkinUrl skinUrl = SkinUrl.builder()
            .s3Url("test")
            .cloudfrontUrl("test")
            .build();

    private final StoryUrlDto storyUrlDto = StoryUrlDto.builder()
            .s3Url("test")
            .cloudfrontUrl("test")
            .build();

    private final StoryUrl storyUrl = StoryUrl.builder()
            .s3Url("test")
            .cloudfrontUrl("test")
            .build();

    private final UUID userId = UUID.randomUUID();

    private final User user = User.builder()
            .id(userId)
            .nickname("test")
            .email("test")
            .password("test")
            .phoneNumber("test")
            .build();

    private final SkinLocationDto skinLocationDto = SkinLocationDto.builder()
            .latitude(1.0)
            .longitude(1.0)
            .country("test")
            .state("test")
            .city("test")
            .build();

    private final SkinLocation skinLocation = SkinLocation.builder()
            .latitude(1.0)
            .longitude(1.0)
            .country("test")
            .state("test")
            .city("test")
            .build();

    private final SkinTimeDto skinTimeDto = SkinTimeDto.builder()
            .year(2021)
            .month(1)
            .day(1)
            .hour(1)
            .minute(1)
            .build();

    private final SkinTime skinTime = SkinTime.builder()
            .year(2021)
            .month(1)
            .day(1)
            .hour(1)
            .minute(1)
            .build();

    private final PostDto.PostCreateRequest postCreateRequest = PostDto.PostCreateRequest.builder()
            .location(skinLocationDto)
            .time(skinTimeDto)
            .isPublic(true)
            .build();

    @Test
    public void createPost_returnsPostDto_isNotNull() {
        // given
        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinLocation(skinLocation)
                .skinTime(skinTime)
                .user(user)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        when(postRepository.save(any(Post.class))).thenReturn(post);
        final PostDto postDto = postService.createPost(user, skinUrlDto, storyUrlDto, postCreateRequest);

        // then
        assertThat(postDto).isNotNull();
        assertThat(postDto.getUserId()).isNotNull();
        assertThat(postDto.getSkin()).isNotNull();
        assertThat(postDto.getStory()).isNotNull();
        assertThat(postDto.getLocation()).isNotNull();
        assertThat(postDto.getTime()).isNotNull();
        assertThat(postDto.getIsPublic()).isNotNull();
        assertThat(postDto.getCreatedAt()).isNotNull();
    }

    @Test
    public void getPost_returnsPostDto_isNotNull() {
        // given
        final UUID postId = UUID.randomUUID();
        final Post post = Post.builder()
                .id(postId)
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinLocation(skinLocation)
                .skinTime(skinTime)
                .user(user)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        final PostDto.PostReadResponse postReadResponse = postService.getPost(postId);

        // then
        assertThat(postReadResponse).isNotNull();
        assertThat(postReadResponse.getPostId()).isNotNull();
        assertThat(postReadResponse.getUserId()).isNotNull();
        assertThat(postReadResponse.getSkin()).isNotNull();
        assertThat(postReadResponse.getStory()).isNotNull();
        assertThat(postReadResponse.getLocation()).isNotNull();
        assertThat(postReadResponse.getTime()).isNotNull();
        assertThat(postReadResponse.getIsPublic()).isNotNull();
        assertThat(postReadResponse.getFavoriteCount()).isNotNull();
        assertThat(postReadResponse.getCommentCount()).isNotNull();
        assertThat(postReadResponse.getLikeCount()).isNotNull();
        assertThat(postReadResponse.getViewCount()).isNotNull();
        assertThat(postReadResponse.getCreatedAt()).isNotNull();
    }

    @Test
    public void getPost_whenNull_error() {
        // given
        final UUID postId = UUID.randomUUID();

        // when
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.empty());

        final PostException postException = assertThrows(PostException.class, () -> postService.getPost(postId));

        // then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.POST_NOT_FOUND);}
}
