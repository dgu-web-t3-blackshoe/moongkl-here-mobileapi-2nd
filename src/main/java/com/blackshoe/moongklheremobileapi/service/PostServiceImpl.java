package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.blackshoe.moongklheremobileapi.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

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

    @Override
    public PostDto.PostReadResponse getPost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post not found. postId: {}", postId);
            throw new PostException(PostErrorResult.POST_NOT_FOUND);
        });

        final SkinLocationDto skinLocationDto = SkinLocationDto.builder()
                .latitude(post.getSkinLocation().getLatitude())
                .longitude(post.getSkinLocation().getLongitude())
                .country(post.getSkinLocation().getCountry())
                .state(post.getSkinLocation().getState())
                .city(post.getSkinLocation().getCity())
                .build();

        final SkinTimeDto skinTimeDto = SkinTimeDto.builder()
                .year(post.getSkinTime().getYear())
                .month(post.getSkinTime().getMonth())
                .day(post.getSkinTime().getDay())
                .hour(post.getSkinTime().getHour())
                .minute(post.getSkinTime().getMinute())
                .build();

        final PostDto.PostReadResponse postReadResponse = PostDto.PostReadResponse.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .skin(post.getSkinUrl().getCloudfrontUrl())
                .story(post.getStoryUrl().getCloudfrontUrl())
                .location(skinLocationDto)
                .time(skinTimeDto)
                .favoriteCount(post.getFavoriteCount())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isPublic(post.isPublic())
                .createdAt(post.getCreatedAt().toString())
                .build();

        return postReadResponse;
    }

    @Override
    public Page<PostDto.PostListReadResponse> getPostList(String from, String to,
                                                          String location, Double latitude, Double longitude, Double radius,
                                                          String sort, Integer size, Integer page) {

        final PostTimeFilter postTimeFilter = PostTimeFilter.verifyAndConvertStringToPostTimeFilter(from, to);

        final PostPointFilter postPointFilter = PostPointFilter.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .build();

        final LocationType locationType = LocationType.verifyAndConvertStringToLocationType(location);

        final SortType sortType = SortType.verifyAndConvertStringToSortType(sort);

        final Sort sortBy = Sort.by(Sort.Direction.DESC, SortType.getSortField(sortType));

        final Pageable pageable = PageRequest.of(page, size, sortBy);

        final Page<PostDto.PostListReadResponse> postListReadResponsePage;

        switch (locationType) {
            case DOMESTIC:
                postListReadResponsePage
                        = postRepository.findAllBySkinTimeBetweenAndDomesticAndIsPublic(postTimeFilter, pageable);
                return postListReadResponsePage;
            case ABROAD:
                postListReadResponsePage
                        = postRepository.findAllBySkinTimeBetweenAndAbroadAndIsPublic(postTimeFilter, pageable);
                return postListReadResponsePage;
            case CURRENT:
                postListReadResponsePage
                        = postRepository.findAllBySkinTimeBetweenAndCurrentLocationAndIsPublic(
                        postTimeFilter,
                        postPointFilter,
                        pageable);
                return postListReadResponsePage;
            case DEFAULT:
                postListReadResponsePage = postRepository.findAllBySkinTimeBetweenAndIsPublic(postTimeFilter, pageable);
                return postListReadResponsePage;
        }

        throw new PostException(PostErrorResult.GET_POST_LIST_FAILED);
    }

    @Override
    public Page<PostDto.PostGroupByCityReadResponse> getUserPostListGroupedByCity(User user, Double latitude, Double longitude, Double radius, Integer size, Integer page) {

        final PostPointFilter postPointFilter = PostPointFilter.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .build();

        final Pageable pageable = PageRequest.of(page, size);

        final Page<PostDto.PostGroupByCityReadResponse> postGroupByCityReadResponsePage
                = postRepository.findAllUserPostByLocationAndGroupByCity(user, postPointFilter, pageable);

        return postGroupByCityReadResponsePage;
    }

    @Override
    public Page<PostDto.PostListReadResponse> getUserCityPostList(User user, String country, String state, String city, String sort, Integer size, Integer page) {

        final PostAddressFilter postAddressFilter = PostAddressFilter.builder()
                .country(country)
                .state(state)
                .city(city)
                .build();

        final SortType sortType = SortType.verifyAndConvertStringToSortType(sort);

        final Sort sortBy = Sort.by(Sort.Direction.DESC, SortType.getSortField(sortType));
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        final Page<PostDto.PostListReadResponse> userCityPostReadResponsePage
                = postRepository.findAllUserPostByCity(user, postAddressFilter, pageable);

        return userCityPostReadResponsePage;
    }

    @Override
    public Page<PostDto.PostListReadResponse> getUserSkinTimePostList(User user, String from, String to, String sort, Integer size, Integer page) {

        final PostTimeFilter postTimeFilter = PostTimeFilter.verifyAndConvertStringToPostTimeFilter(from, to);

        final SortType sortType = SortType.verifyAndConvertStringToSortType(sort);

        final Sort sortBy = Sort.by(Sort.Direction.DESC, SortType.getSortField(sortType));
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        final Page<PostDto.PostListReadResponse> userSkinTimePostReadResponsePage
                = postRepository.findAllUserPostBySkinTime(user, postTimeFilter, pageable);

        return userSkinTimePostReadResponsePage;
    }
}
