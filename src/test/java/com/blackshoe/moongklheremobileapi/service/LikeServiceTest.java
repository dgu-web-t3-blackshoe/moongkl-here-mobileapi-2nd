package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.repository.LikeRepository;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeServiceImpl likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger log = LoggerFactory.getLogger(ViewServiceTest.class);

    private final UUID userId = UUID.randomUUID();

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

    private final UUID postId = UUID.randomUUID();

    private final Post post = Post.builder()
            .id(postId)
            .skinUrl(skinUrl)
            .storyUrl(storyUrl)
            .skinLocation(skinLocation)
            .skinTime(skinTime)
            .user(user)
            .isPublic(true)
            .createdAt(LocalDateTime.now())
            .build();

    @Test
    public void likePost_whenSuccess_createLike() {
        // given
        final Like like = Like.builder()
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
        when(likeRepository.existsByPostAndUser(any(), any())).thenReturn(false);
        final PostDto.LikePostDto likePostDto =  likeService.likePost(postId, user);

        // then
        assertThat(likePostDto.getLikeCount()).isEqualTo(1);
    }

    @Test
    public void dislikePost_whenSuccess_deleteLike() {
        // given
        final Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
        when(likeRepository.findByPostAndUser(any(), any())).thenReturn(Optional.ofNullable(like));

        // when
        final PostDto.LikePostDto likePostDto = likeService.likePost(postId, user);
        final PostDto.DislikePostDto dislikePostDto = likeService.dislikePost(postId, user);

        // then
        assertThat(likePostDto.getLikeCount()).isEqualTo(1);
        assertThat(dislikePostDto.getLikeCount()).isEqualTo(0);
    }

    @Test
    public void getUserLikedPostList_whenSuccess_returnsFavoritePostPage() {
        // given
        final Page mockPage = new PageImpl(new ArrayList());
        final int size = 10;
        final int page = 0;

        // when
        when(likeRepository.findAllLikedPostByUser(any(User.class), any(Pageable.class))).thenReturn(mockPage);
        final Page<PostDto.PostListReadResponse> userFavoritePostResponse
                = likeService.getUserLikedPostList(user, size, page);

        // then
        assertThat(userFavoritePostResponse).isNotNull();
    }
}
