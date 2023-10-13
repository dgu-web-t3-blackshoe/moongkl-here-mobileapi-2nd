package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.repository.TemporaryPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Service
public class TemporaryPostServiceImpl implements TemporaryPostService {

    private final TemporaryPostRepository temporaryPostRepository;

    public TemporaryPostServiceImpl(TemporaryPostRepository temporaryPostRepository) {
        this.temporaryPostRepository = temporaryPostRepository;
    }

    @Override
    @Transactional
    public TemporaryPostDto createTemporaryPost(User user,
                                                SkinUrlDto uploadedSkinUrl,
                                                StoryUrlDto uploadedStoryUrl,
                                                TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest) {
        final SkinUrl skinUrl = SkinUrl.convertSkinUrlDtoToEntity(uploadedSkinUrl);

        final StoryUrl storyUrl = StoryUrl.convertStoryUrlDtoToEntity(uploadedStoryUrl);

        final SkinLocation skinLocation = getSkinLocationFromTemporaryPostCreateRequest(temporaryPostCreateRequest);

        final SkinTime skinTime = getSkinTimeFromTemporaryPostCreateRequest(temporaryPostCreateRequest);

        final TemporaryPost temporaryPost = TemporaryPost.builder()
                .user(user)
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinLocation(skinLocation)
                .skinTime(skinTime)
                .build();

        final TemporaryPost savedTemporaryPost = temporaryPostRepository.save(temporaryPost);

        final TemporaryPostDto temporaryPostDto = convertTemporaryPostEntityToDto(skinUrl, storyUrl, savedTemporaryPost);

        return temporaryPostDto;
    }

    private TemporaryPostDto convertTemporaryPostEntityToDto(SkinUrl skinUrl, StoryUrl storyUrl, TemporaryPost savedTemporaryPost) {
        final SkinLocationDto skinLocationDto = SkinLocationDto.builder()
                .latitude(savedTemporaryPost.getSkinLocation().getLatitude())
                .longitude(savedTemporaryPost.getSkinLocation().getLongitude())
                .country(savedTemporaryPost.getSkinLocation().getCountry())
                .state(savedTemporaryPost.getSkinLocation().getState())
                .city(savedTemporaryPost.getSkinLocation().getCity())
                .build();

        final SkinTimeDto skinTimeDto = SkinTimeDto.builder()
                .year(savedTemporaryPost.getSkinTime().getYear())
                .month(savedTemporaryPost.getSkinTime().getMonth())
                .day(savedTemporaryPost.getSkinTime().getDay())
                .hour(savedTemporaryPost.getSkinTime().getHour())
                .minute(savedTemporaryPost.getSkinTime().getMinute())
                .build();

        final TemporaryPostDto temporaryPostDto = TemporaryPostDto.builder()
                .postId(savedTemporaryPost.getId())
                .userId(savedTemporaryPost.getUser().getId())
                .skin(skinUrl.getCloudfrontUrl())
                .story(storyUrl.getCloudfrontUrl())
                .location(skinLocationDto)
                .time(skinTimeDto)
                .createdAt(savedTemporaryPost.getCreatedAt())
                .build();

        return temporaryPostDto;
    }

    private SkinTime getSkinTimeFromTemporaryPostCreateRequest(
            TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest) {

        final SkinTimeDto skinTimeDto = temporaryPostCreateRequest.getTime();

        final SkinTime skinTime = SkinTime.builder()
                .year(skinTimeDto.getYear())
                .month(skinTimeDto.getMonth())
                .day(skinTimeDto.getDay())
                .hour(skinTimeDto.getHour())
                .minute(skinTimeDto.getMinute())
                .build();

        return skinTime;
    }

    private SkinLocation getSkinLocationFromTemporaryPostCreateRequest(
            TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest) {

        final SkinLocationDto skinLocationDto = temporaryPostCreateRequest.getLocation();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(skinLocationDto.getLatitude())
                .longitude(skinLocationDto.getLongitude())
                .country(skinLocationDto.getCountry())
                .state(skinLocationDto.getState())
                .city(skinLocationDto.getCity())
                .build();

        return skinLocation;
    }
}
