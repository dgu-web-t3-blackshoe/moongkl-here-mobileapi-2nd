package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.*;
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

    private final SkinUrlRepository skinUrlRepository;

    private final StoryUrlRepository storyUrlRepository;

    private final SkinLocationRepository skinLocationRepository;

    private final SkinTimeRepository skinTimeRepository;

    private final LikeRepository likeRepository;

    private final FavoriteRepository favoriteRepository;

    private final ViewRepository viewRepository;

    private final SkinService skinService;

    private final StoryService storyService;

    public PostServiceImpl(PostRepository postRepository,
                           SkinUrlRepository skinUrlRepository,
                           StoryUrlRepository storyUrlRepository,
                           SkinLocationRepository skinLocationRepository,
                           SkinTimeRepository skinTimeRepository,
                           LikeRepository likeRepository,
                           FavoriteRepository favoriteRepository,
                           ViewRepository viewRepository,
                           SkinService skinService,
                           StoryService storyService) {
        this.postRepository = postRepository;
        this.skinUrlRepository = skinUrlRepository;
        this.storyUrlRepository = storyUrlRepository;
        this.skinLocationRepository = skinLocationRepository;
        this.skinTimeRepository = skinTimeRepository;
        this.likeRepository = likeRepository;
        this.favoriteRepository = favoriteRepository;
        this.viewRepository = viewRepository;
        this.skinService = skinService;
        this.storyService = storyService;
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

        final Boolean isPublic = Boolean.valueOf(postCreateRequest.getIsPublic());

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
                .updatedAt(savedPost.getUpdatedAt())
                .build();

        return postDto;
    }

    @Override
    @Transactional
    public PostDto changePostIsPublic(User user, UUID postId, Boolean isPublic) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post not found. postId: {}", postId);
            throw new PostException(PostErrorResult.POST_NOT_FOUND);
        });

        if (post.getUser().getId() != user.getId()) {
            log.error("User does not have permission to change post isPublic. postId: {}", postId);
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        post.changeIsPublic(isPublic);

        final PostDto postDto = convertPostEntityToDto(post.getSkinUrl(), post.getStoryUrl(), post);

        return postDto;
    }

    @Override
    @Transactional
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

    @Override
    public PostDto saveTemporaryPost(User user, TemporaryPostDto.TemporaryPostToSave temporaryPostToSave, Boolean isPublic) {

        final SkinUrl skinUrl = skinUrlRepository.findById(temporaryPostToSave.getSkinUrlId())
                .orElseThrow(() -> new PostException(PostErrorResult.SKIN_URL_NOT_FOUND));

        final StoryUrl storyUrl = storyUrlRepository.findById(temporaryPostToSave.getStoryUrlId())
                .orElseThrow(() -> new PostException(PostErrorResult.STORY_URL_NOT_FOUND));

        final SkinLocation skinLocation = skinLocationRepository.findById(temporaryPostToSave.getSkinLocationId())
                .orElseThrow(() -> new PostException(PostErrorResult.SKIN_LOCATION_NOT_FOUND));

        final SkinTime skinTime = skinTimeRepository.findById(temporaryPostToSave.getSkinTimeId())
                .orElseThrow(() -> new PostException(PostErrorResult.SKIN_TIME_NOT_FOUND));

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

    @Override
    @Transactional
    public void deletePost(User user, UUID postId) {

            final Post post = postRepository.findById(postId).orElseThrow(() -> {
                throw new PostException(PostErrorResult.POST_NOT_FOUND);
            });

            log.info(String.valueOf(post.getUser().getClass()));
            log.info(String.valueOf(user.getClass()));

            if (!post.getUser().getId().equals(user.getId())) {
                throw new PostException(PostErrorResult.USER_NOT_MATCH);
            }

            likeRepository.deleteAllByPost(post);

            favoriteRepository.deleteAllByPost(post);

            viewRepository.deleteAllByPost(post);

            skinService.deleteSkin(post.getSkinUrl().getS3Url());

            storyService.deleteStory(post.getStoryUrl().getS3Url());

            postRepository.delete(post);

    }
}
