package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.repository.SkinLocationRepository;
import com.blackshoe.moongklheremobileapi.repository.SkinTimeRepository;
import com.blackshoe.moongklheremobileapi.repository.TemporaryPostRepository;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TemporaryPostServiceTest {

    @InjectMocks
    private TemporaryPostServiceImpl temporaryPostService;

    @Mock
    private TemporaryPostRepository temporaryPostRepository;

    @Mock
    private SkinLocationRepository skinLocationRepository;

    @Mock
    private SkinTimeRepository skinTimeRepository;

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

    private final TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest
            = TemporaryPostDto.TemporaryPostCreateRequest.builder()
            .location(skinLocationDto)
            .time(skinTimeDto)
            .build();

    @Test
    public void createTemporaryPost_returnsTemporaryPostDto_istNotNull() {
        // given
        final TemporaryPost temporaryPost = TemporaryPost.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinLocation(skinLocation)
                .skinTime(skinTime)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        given(temporaryPostRepository.save(any(TemporaryPost.class))).willReturn(temporaryPost);
        final TemporaryPostDto temporaryPostDto
                = temporaryPostService.createTemporaryPost(user, skinUrlDto, storyUrlDto, temporaryPostCreateRequest);

        // then
        assertThat(temporaryPostDto).isNotNull();
        assertThat(temporaryPostDto.getUserId()).isNotNull();
        assertThat(temporaryPostDto.getSkin()).isNotNull();
        assertThat(temporaryPostDto.getStory()).isNotNull();
        assertThat(temporaryPostDto.getLocation()).isNotNull();
        assertThat(temporaryPostDto.getTime()).isNotNull();
        assertThat(temporaryPostDto.getCreatedAt()).isNotNull();
    }

}
