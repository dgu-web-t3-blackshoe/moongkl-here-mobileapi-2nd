package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.TemporaryPostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.TemporaryPostException;
import com.blackshoe.moongklheremobileapi.repository.TemporaryPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                .temporaryPostId(savedTemporaryPost.getId())
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

    @Override
    public Page<TemporaryPostDto.TemporaryPostListReadResponse> getUserTemporaryPostList(User user, Integer size, Integer page) {

        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");

        final Pageable pageable = PageRequest.of(page, size, sortBy);

        final Page<TemporaryPostDto.TemporaryPostListReadResponse> temporaryPostListReadResponsePage
                = temporaryPostRepository.findAllByUser(user, pageable);

        return temporaryPostListReadResponsePage;
    }

    @Override
    public TemporaryPostDto getTemporaryPost(UUID temporaryPostId, User user) {

        final TemporaryPost temporaryPost = temporaryPostRepository.findById(temporaryPostId)
                .orElseThrow(() -> new TemporaryPostException(TemporaryPostErrorResult.TEMPORARY_POST_NOT_FOUND));

        if (!temporaryPost.getUser().getId().equals(user.getId())) {
            throw new TemporaryPostException(TemporaryPostErrorResult.USER_NOT_MATCH);
        }

        final SkinUrl skinUrl = temporaryPost.getSkinUrl();

        final StoryUrl storyUrl = temporaryPost.getStoryUrl();

        final TemporaryPostDto temporaryPostDto = convertTemporaryPostEntityToDto(skinUrl, storyUrl, temporaryPost);

        return temporaryPostDto;
    }

    @Override
    public void deleteTemporaryPost(UUID uuid, User user) {

        final TemporaryPost temporaryPost = temporaryPostRepository.findById(uuid)
                .orElseThrow(() -> new TemporaryPostException(TemporaryPostErrorResult.TEMPORARY_POST_NOT_FOUND));

        if (!temporaryPost.getUser().getId().equals(user.getId())) {
            throw new TemporaryPostException(TemporaryPostErrorResult.USER_NOT_MATCH);
        }

        temporaryPostRepository.delete(temporaryPost);
    }
}
