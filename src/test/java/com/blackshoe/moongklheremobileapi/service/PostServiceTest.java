package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.*;
import com.blackshoe.moongklheremobileapi.vo.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.*;

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

    @Mock
    private SkinUrlRepository skinUrlRepository;

    @Mock
    private StoryUrlRepository storyUrlRepository;

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
            .isPublic("true")
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
    public void changePostIsPublic_returnsPostDto_isValid() {
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
        final PostDto postDto = postService.changePostIsPublic(user, postId, false);

        // then
        assertThat(postDto.getIsPublic()).isEqualTo(false);
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
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.POST_NOT_FOUND);
    }

    @Test
    public void getPostList_whenDefaultSuccess_isNotNull() {
        // given
        final Page<PostDto.PostListReadResponse> mockPostListReadResponsePage =  new PageImpl<>(new ArrayList<>());

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final String location = LocationType.DEFAULT.getLocationType();
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final String sort = SortType.DEFAULT.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        when(postRepository.findAllBySkinTimeBetweenAndIsPublic(
                any(PostTimeFilter.class),
                any(Pageable.class))).thenReturn(mockPostListReadResponsePage);
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
    }

    @Test
    public void getPostList_whenDomesticSuccess_isNotNull() {
        // given
        Page<PostDto.PostListReadResponse> mockPostListReadResponsePage =  new PageImpl<>(new ArrayList<>());

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final String location = LocationType.DOMESTIC.getLocationType();
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final String sort = SortType.LIKES.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        when(postRepository.findAllBySkinTimeBetweenAndDomesticAndIsPublic(
                any(PostTimeFilter.class),
                any(Pageable.class))).thenReturn(mockPostListReadResponsePage);
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
    }

    @Test
    public void getPostList_whenAbroadSuccess_isNotNull() {
        // given
        Page<PostDto.PostListReadResponse> mockPostListReadResponsePage =  new PageImpl<>(new ArrayList<>());

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final String location = LocationType.ABROAD.getLocationType();
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final String sort = SortType.VIEWS.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        when(postRepository.findAllBySkinTimeBetweenAndAbroadAndIsPublic(
                any(PostTimeFilter.class),
                any(Pageable.class))).thenReturn(mockPostListReadResponsePage);
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
    }

    @Test
    public void getPostList_whenCurrentLocationSuccess_isNotNull() {
        // given
        Page<PostDto.PostListReadResponse> mockPostListReadResponsePage =  new PageImpl<>(new ArrayList<>());

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final String location = LocationType.CURRENT.getLocationType();
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final String sort = SortType.DEFAULT.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        when(postRepository.findAllBySkinTimeBetweenAndCurrentLocationAndIsPublic(
                any(PostTimeFilter.class),
                any(PostPointFilter.class),
                any(Pageable.class))).thenReturn(mockPostListReadResponsePage);
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
    }

    @Test
    public void getPostList_whenInvalidLocationType_throwsPostException() {
        // given
        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final String location = "invalid";
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final String sort = SortType.DEFAULT.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        final PostException postException = assertThrows(PostException.class, () -> postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page));

        // then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_LOCATION_TYPE);
    }

    @Test
    public void getPostList_whenInvalidDateFormat_throwsPostException() {
        // given
        final String from = "2023-011";
        final String to = "2023-1231";
        final String location = LocationType.DEFAULT.getLocationType();
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final String sort = SortType.DEFAULT.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        final PostException postException = assertThrows(PostException.class, () -> postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page));

        // then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_DATE_FORMAT);
    }

    @Test
    public void getPostList_whenInvalidSortType_throwsPostException() {
        // given
        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final String location = LocationType.DEFAULT.getLocationType();
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final String sort = "invalid";
        final Integer size = 10;
        final Integer page = 0;

        // when
        final PostException postException = assertThrows(PostException.class, () -> postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page));

        // then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_SORT_TYPE);
    }

    @Test
    public void getUserPostListGroupedByCity_whenSuccess_isNotNull() {
        // given
        Page<PostDto.PostGroupByCityReadResponse> mockUserPostListGroupedByCityReadResponseList = new PageImpl<>(new ArrayList<>());

        final User user = new User();
        final Double longitude = 0.0;
        final Double latitude = 0.0;
        final Double radius = 0.0;
        final Integer size = 10;
        final Integer page = 0;

        // when
        when(postRepository.findAllUserPostByLocationAndGroupByCity(
                any(User.class),
                any(PostPointFilter.class),
                any(Pageable.class))).thenReturn(mockUserPostListGroupedByCityReadResponseList);
        final Page<PostDto.PostGroupByCityReadResponse> userPostListGroupedByCityReadResponsePage
                = postService.getUserPostListGroupedByCity(user, latitude, longitude, radius, size, page);

        // then
        assertThat(userPostListGroupedByCityReadResponsePage).isNotNull();
    }

    @Test
    public void getUserCityPostList_whenSuccess_isNotNull() {
        // given
        Page<PostDto.PostListReadResponse> mockCityUserPostListReadResponseList = new PageImpl<>(new ArrayList<>());

        final User user = new User();
        final String country = "country";
        final String state = "state";
        final String city = "city";
        final String sort = SortType.DEFAULT.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        when(postRepository.findAllUserPostByCity(
                any(User.class),
                any(PostAddressFilter.class),
                any(Pageable.class))).thenReturn(mockCityUserPostListReadResponseList);

        final Page<PostDto.PostListReadResponse> userCityPostListReadResponsePage
                = postService.getUserCityPostList(user, country, state, city, sort, size, page);

        // then
        assertThat(userCityPostListReadResponsePage).isNotNull();
    }

    @Test
    public void getUserSkinTimePostList_whenSuccess_isNotNull() {
        // given
        Page<PostDto.PostListReadResponse> mockUserSkinTimePostListReadResponseList = new PageImpl<>(new ArrayList<>());

        final User user = new User();
        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final String sort = SortType.DEFAULT.getSortType();
        final Integer size = 10;
        final Integer page = 0;

        // when
        when(postRepository.findAllUserPostBySkinTime(
                any(User.class),
                any(PostTimeFilter.class),
                any(Pageable.class))).thenReturn(mockUserSkinTimePostListReadResponseList);

        final Page<PostDto.PostListReadResponse> userSkinTimePostListReadResponsePage
                = postService.getUserSkinTimePostList(user, from, to, sort, size, page);

        // then
        assertThat(userSkinTimePostListReadResponsePage).isNotNull();
    }

    @Test
    public void saveTemporaryPost_whenSuccess_isNotNull() {
        // given
        final UUID skinUrlId = UUID.randomUUID();
        final UUID storyUrlId = UUID.randomUUID();
        final UUID skinLocationId = UUID.randomUUID();
        final UUID skinTimeId = UUID.randomUUID();

        final SkinUrl skinUrlWithId = SkinUrl.builder()
                .id(skinUrlId)
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrlWithId = StoryUrl.builder()
                .id(storyUrlId)
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinLocation skinLocationWithId = SkinLocation.builder()
                .id(skinLocationId)
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final SkinTime skinTimeWithId = SkinTime.builder()
                .id(skinTimeId)
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final TemporaryPostDto.TemporaryPostToSave temporaryPostToSave = TemporaryPostDto.TemporaryPostToSave.builder()
                .skinUrlId(skinUrlId)
                .storyUrlId(storyUrlId)
                .skinLocationId(skinLocationId)
                .skinTimeId(skinTimeId)
                .build();

        final Post post = Post.builder()
                .skinUrl(skinUrlWithId)
                .storyUrl(storyUrlWithId)
                .skinLocation(skinLocationWithId)
                .skinTime(skinTimeWithId)
                .user(user)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(skinUrlRepository.findById(skinUrlId)).thenReturn(java.util.Optional.of(skinUrlWithId));
        when(storyUrlRepository.findById(storyUrlId)).thenReturn(java.util.Optional.of(storyUrlWithId));
        when(skinLocationRepository.findById(skinLocationId)).thenReturn(java.util.Optional.of(skinLocationWithId));
        when(skinTimeRepository.findById(skinTimeId)).thenReturn(java.util.Optional.of(skinTimeWithId));
        final PostDto postDto = postService.saveTemporaryPost(user, temporaryPostToSave, true);

        // then
        assertThat(postDto).isNotNull();
        assertThat(postDto.getUserId()).isNotNull();
        assertThat(postDto.getSkin()).isEqualTo(skinUrlWithId.getCloudfrontUrl());
        assertThat(postDto.getStory()).isEqualTo(storyUrlWithId.getCloudfrontUrl());
        assertThat(postDto.getLocation().getCity()).isEqualTo(skinLocationWithId.getCity());
        assertThat(postDto.getTime().getYear()).isEqualTo(skinTimeWithId.getYear());
        assertThat(postDto.getIsPublic()).isTrue();
        assertThat(postDto.getCreatedAt()).isNotNull();
    }
}
