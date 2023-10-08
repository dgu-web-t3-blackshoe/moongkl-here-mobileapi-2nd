package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public PostDto createPost(User user,
                              SkinUrlDto uploadedSkinUrl,
                              StoryUrlDto uploadedStoryUrl,
                              PostDto.PostCreateRequest postCreateRequest) {

        final SkinUrl skinUrl = SkinUrl.convertSkinUrlDtoToEntity(uploadedSkinUrl);

        final StoryUrl storyUrl = StoryUrl.convertStoryUrlDtoToEntity(uploadedStoryUrl);

        final SkinLocation skinLocation = getSkinLocationFromPostCreateRequest(postCreateRequest);

        final SkinTime skinTime = getSkinTimeFromPostCreateRequest(postCreateRequest);

        final Boolean isPublic = postCreateRequest.getIsPublic();

        final Post post = Post.builder()
                .user(user)
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinLocation(skinLocation)
                .skinTime(skinTime)
                .isPublic(isPublic)
                .build();

        final Post savedPost = postRepository.save(post);

        final PostDto postDto = convertPostEntityToDto(skinUrl, storyUrl, savedPost);

        return postDto;
    }

    private static SkinLocation getSkinLocationFromPostCreateRequest(PostDto.PostCreateRequest postCreateRequest) {
        final SkinLocationDto skinLocationDto = postCreateRequest.getLocation();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(skinLocationDto.getLatitude())
                .longitude(skinLocationDto.getLongitude())
                .country(skinLocationDto.getCountry())
                .state(skinLocationDto.getState())
                .city(skinLocationDto.getCity())
                .build();

        return skinLocation;
    }

    private static SkinTime getSkinTimeFromPostCreateRequest(PostDto.PostCreateRequest postCreateRequest) {
        final SkinTimeDto skinTimeDto = postCreateRequest.getTime();

        final SkinTime skinTime = SkinTime.builder()
                .year(skinTimeDto.getYear())
                .month(skinTimeDto.getMonth())
                .day(skinTimeDto.getDay())
                .hour(skinTimeDto.getHour())
                .minute(skinTimeDto.getMinute())
                .build();

        return skinTime;
    }

    private static PostDto convertPostEntityToDto(SkinUrl skinUrl, StoryUrl storyUrl, Post savedPost) {
        final SkinLocationDto skinLocationDto = SkinLocationDto.builder()
                .latitude(savedPost.getSkinLocation().getLatitude())
                .longitude(savedPost.getSkinLocation().getLongitude())
                .country(savedPost.getSkinLocation().getCountry())
                .state(savedPost.getSkinLocation().getState())
                .city(savedPost.getSkinLocation().getCity())
                .build();

        final SkinTimeDto skinTimeDto = SkinTimeDto.builder()
                .year(savedPost.getSkinTime().getYear())
                .month(savedPost.getSkinTime().getMonth())
                .day(savedPost.getSkinTime().getDay())
                .hour(savedPost.getSkinTime().getHour())
                .minute(savedPost.getSkinTime().getMinute())
                .build();

        final PostDto postDto = PostDto.builder()
                .postId(savedPost.getId())
                .userId(savedPost.getUser().getId())
                .skin(skinUrl.getCloudfrontUrl())
                .story(storyUrl.getCloudfrontUrl())
                .location(skinLocationDto)
                .time(skinTimeDto)
                .isPublic(savedPost.isPublic())
                .createdAt(savedPost.getCreatedAt())
                .build();

        return postDto;
    }
}
