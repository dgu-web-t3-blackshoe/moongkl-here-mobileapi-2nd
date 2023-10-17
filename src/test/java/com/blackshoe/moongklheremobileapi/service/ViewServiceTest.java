package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import com.blackshoe.moongklheremobileapi.repository.ViewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ViewServiceTest {

    @InjectMocks
    private ViewServiceImpl viewService;

    @Mock
    private ViewRepository viewRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

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
    public void increaseViewCount_whenFirstRequest_increase1L() throws Exception {
        //given
        final UUID viewId = UUID.randomUUID();

        final View view = View.builder()
                .id(viewId)
                .post(post)
                .user(user)
                .lastViewedAt(LocalDateTime.now())
                .build();

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(viewRepository.findByPostAndUser(post, user)).thenReturn(Optional.empty());
        when(viewRepository.save(any(View.class))).thenReturn(view);
        final PostDto.IncreaseViewCountDto increaseViewCountDto = viewService.increaseViewCount(postId, user);

        //then
        log.info("lastViewedAt = {}", increaseViewCountDto.getLastViewedAt());
        assertThat(increaseViewCountDto.getViewCount()).isEqualTo(1L);
    }

    @Test
    public void increaseViewCount_whenSecondRequestWithIn1Day_increase0L() throws Exception {
        //given
        final UUID viewId = UUID.randomUUID();

        final View view = View.builder()
                .id(viewId)
                .post(post)
                .user(user)
                .lastViewedAt(LocalDateTime.now())
                .build();

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(viewRepository.findByPostAndUser(post, user)).thenReturn(Optional.ofNullable(view));
        final PostDto.IncreaseViewCountDto increaseViewCountDto = viewService.increaseViewCount(postId, user);

        //then
        log.info("lastViewedAt = {}", increaseViewCountDto.getLastViewedAt());
        assertThat(increaseViewCountDto.getViewCount()).isEqualTo(0L);
    }
}
