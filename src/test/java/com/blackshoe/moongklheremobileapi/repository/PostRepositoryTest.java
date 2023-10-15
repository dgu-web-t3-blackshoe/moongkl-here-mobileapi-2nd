package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.vo.PostPointFilter;
import com.blackshoe.moongklheremobileapi.vo.PostTimeFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger log = LoggerFactory.getLogger(PostRepositoryTest.class);

    @Test
    public void assert_isNotNull() {
        assertThat(postRepository).isNotNull();
    }

    @Test
    public void save_returns_isNotNull() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .user(user)
                .likeCount(10)
                .favoriteCount(100)
                .viewCount(20)
                .isPublic(true)
                .build();

        //when
        final Post savedPost = postRepository.save(post);

        //then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();
        log.info("savedPost.getId() = {}", savedPost.getId());
        assertThat(savedPost.getSkinUrl()).isEqualTo(skinUrl);
        log.info("skinUrl.getId() = {}", savedPost.getSkinUrl().getId());
        assertThat(savedPost.getStoryUrl()).isEqualTo(storyUrl);
        log.info("storyUrl.getId() = {}", savedPost.getStoryUrl().getId());
        assertThat(savedPost.getSkinTime()).isEqualTo(skinTime);
        log.info("skinTime.getId() = {}", savedPost.getSkinTime().getId());
        assertThat(savedPost.getSkinLocation()).isEqualTo(skinLocation);
        log.info("skinLocation.getId() = {}", savedPost.getSkinLocation().getId());
        assertThat(savedPost.getUser()).isEqualTo(user);
        log.info("user.getId() = {}", savedPost.getUser().getId());
        assertThat(savedPost.getLikeCount()).isEqualTo(10);
        assertThat(savedPost.getFavoriteCount()).isEqualTo(100);
        assertThat(savedPost.getViewCount()).isEqualTo(20);
        assertThat(savedPost.isPublic()).isEqualTo(true);
        assertThat(savedPost.getCreatedAt()).isNotNull();
        log.info("savedPost.getCreatedAt() = {}", savedPost.getCreatedAt());
    }

    @Test
    public void findById_returns_savedPost() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .user(user)
                .likeCount(10)
                .favoriteCount(100)
                .viewCount(20)
                .isPublic(true)
                .build();

        final Post savedPost = postRepository.save(post);

        //when
        Post foundPost = postRepository.findById(post.getId()).orElse(null);

        //then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getId()).isEqualTo(post.getId());
        assertThat(foundPost.getSkinUrl()).isEqualTo(skinUrl);
        assertThat(foundPost.getStoryUrl()).isEqualTo(storyUrl);
        assertThat(foundPost.getSkinTime()).isEqualTo(skinTime);
        assertThat(foundPost.getSkinLocation()).isEqualTo(skinLocation);
        assertThat(foundPost.getUser()).isEqualTo(user);
        assertThat(foundPost.getLikeCount()).isEqualTo(10);
        assertThat(foundPost.getFavoriteCount()).isEqualTo(100);
        assertThat(foundPost.getViewCount()).isEqualTo(20);
        assertThat(foundPost.isPublic()).isEqualTo(true);
        assertThat(foundPost.getCreatedAt()).isNotNull();
    }

    @Test
    public void findAllBySkinTimeBetweenAndIsPublic_whenDefaultSuccess_isNotNull() throws JsonProcessingException {
        // given
        final SkinLocation domestic = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("대한민국")
                .state("서울특별시")
                .city("강남구")
                .build();

        final SkinLocation foreign = SkinLocation.builder()
                .latitude(1000.0)
                .longitude(1000.0)
                .country("미국")
                .state("캘리포니아")
                .city("로스앤젤레스")
                .build();

        final SkinTime year2020 = SkinTime.builder()
                .year(2020)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinTime year2023 = SkinTime.builder()
                .year(2023)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        for (int idx = 0; idx < 50; idx++) {
            Post post = Post.builder()
                    .skinUrl(skinUrl)
                    .storyUrl(storyUrl)
                    .skinLocation(idx % 2 == 0 ? domestic : foreign)
                    .skinTime(idx % 2 == 0 ? year2023 : year2020)
                    .user(user)
                    .likeCount((long) (Math.random() * 100))
                    .viewCount((long) (Math.random() * 100))
                    .isPublic(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final PostTimeFilter postTimeFilter = PostTimeFilter.verifyAndConvertStringToPostTimeFilter(from, to);
        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Integer size = 10;
        final Integer page = 0;
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        // when
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postRepository.findAllBySkinTimeBetweenAndIsPublic(postTimeFilter, pageable);
        final String result = objectMapper.writeValueAsString(postListReadResponsePage);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
        log.info("postListReadResponsePage = {}", result);
    }

    @Test
    public void findAllBySkinTimeBetweenAndDomesticAndIsPublic_whenDefaultSuccess_isNotNull() throws JsonProcessingException {
        // given
        final SkinLocation domestic = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("대한민국")
                .state("서울특별시")
                .city("강남구")
                .build();

        final SkinLocation foreign = SkinLocation.builder()
                .latitude(1000.0)
                .longitude(1000.0)
                .country("미국")
                .state("캘리포니아")
                .city("로스앤젤레스")
                .build();

        final SkinTime year2020 = SkinTime.builder()
                .year(2020)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinTime year2023 = SkinTime.builder()
                .year(2023)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        for (int idx = 0; idx < 50; idx++) {
            Post post = Post.builder()
                    .skinUrl(skinUrl)
                    .storyUrl(storyUrl)
                    .skinLocation(idx % 2 == 0 ? domestic : foreign)
                    .skinTime(idx % 2 == 0 ? year2023 : year2020)
                    .user(user)
                    .likeCount((long) (Math.random() * 100))
                    .viewCount((long) (Math.random() * 100))
                    .isPublic(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final PostTimeFilter postTimeFilter = PostTimeFilter.verifyAndConvertStringToPostTimeFilter(from, to);
        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Integer size = 10;
        final Integer page = 0;
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        // when
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postRepository.findAllBySkinTimeBetweenAndDomesticAndIsPublic(postTimeFilter, pageable);
        final String result = objectMapper.writeValueAsString(postListReadResponsePage);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
        log.info("postListReadResponsePage = {}", result);
    }

    @Test
    public void findAllBySkinTimeBetweenAndAbroadAndIsPublic_whenDefaultSuccess_isNotNull() throws JsonProcessingException {
        // given
        final SkinLocation domestic = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("대한민국")
                .state("서울특별시")
                .city("강남구")
                .build();

        final SkinLocation foreign = SkinLocation.builder()
                .latitude(1000.0)
                .longitude(1000.0)
                .country("미국")
                .state("캘리포니아")
                .city("로스앤젤레스")
                .build();

        final SkinTime year2020 = SkinTime.builder()
                .year(2020)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinTime year2023 = SkinTime.builder()
                .year(2023)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        for (int idx = 0; idx < 50; idx++) {
            Post post = Post.builder()
                    .skinUrl(skinUrl)
                    .storyUrl(storyUrl)
                    .skinLocation(idx % 2 == 0 ? domestic : foreign)
                    .skinTime(idx % 2 == 0 ? year2020 : year2023)
                    .user(user)
                    .likeCount((long) (Math.random() * 100))
                    .viewCount((long) (Math.random() * 100))
                    .isPublic(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final PostTimeFilter postTimeFilter = PostTimeFilter.verifyAndConvertStringToPostTimeFilter(from, to);
        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Integer size = 10;
        final Integer page = 0;
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        // when
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postRepository.findAllBySkinTimeBetweenAndAbroadAndIsPublic(postTimeFilter, pageable);
        final String result = objectMapper.writeValueAsString(postListReadResponsePage);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
        log.info("postListReadResponsePage = {}", result);
    }

    @Test
    public void findAllBySkinTimeBetweenAndCurrentLocationAndIsPublic_whenDefaultSuccess_isNotNull() throws JsonProcessingException {
        // given
        final SkinLocation domestic = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("대한민국")
                .state("서울특별시")
                .city("강남구")
                .build();

        final SkinLocation foreign = SkinLocation.builder()
                .latitude(1000.0)
                .longitude(1000.0)
                .country("미국")
                .state("캘리포니아")
                .city("로스앤젤레스")
                .build();

        final SkinTime year2020 = SkinTime.builder()
                .year(2020)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinTime year2023 = SkinTime.builder()
                .year(2023)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        for (int idx = 0; idx < 50; idx++) {
            Post post = Post.builder()
                    .skinUrl(skinUrl)
                    .storyUrl(storyUrl)
                    .skinLocation(idx % 2 == 0 ? domestic : foreign)
                    .skinTime(idx % 2 == 0 ? year2020 : year2023)
                    .user(user)
                    .likeCount((long) (Math.random() * 100))
                    .viewCount((long) (Math.random() * 100))
                    .isPublic(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        final String from = "2023-01-01";
        final String to = "2023-12-31";
        final PostTimeFilter postTimeFilter = PostTimeFilter.verifyAndConvertStringToPostTimeFilter(from, to);
        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Integer size = 10;
        final Integer page = 0;
        final Pageable pageable = PageRequest.of(page, size, sortBy);
        final PostPointFilter postPointFilter = PostPointFilter.builder()
                .latitude(0.98)
                .longitude(0.98)
                .radius(3.0)
                .build();

        // when
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postRepository.findAllBySkinTimeBetweenAndCurrentLocationAndIsPublic(postTimeFilter, postPointFilter, pageable);
        final String result = objectMapper.writeValueAsString(postListReadResponsePage);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
        log.info("postListReadResponsePage = {}", result);
    }

    @Test
    public void findAllUserPostByLocation_whenSuccess_isNotNull() throws JsonProcessingException {
        // given
        final SkinLocation domestic = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("대한민국")
                .state("서울특별시")
                .city("강남구")
                .build();

        final SkinLocation foreign = SkinLocation.builder()
                .latitude(1000.0)
                .longitude(1000.0)
                .country("미국")
                .state("캘리포니아")
                .city("로스앤젤레스")
                .build();

        final SkinTime year2020 = SkinTime.builder()
                .year(2020)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinTime year2023 = SkinTime.builder()
                .year(2023)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        final User savedUser = userRepository.save(user);

        final UUID userId = savedUser.getId();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        for (int idx = 0; idx < 50; idx++) {
            Post post = Post.builder()
                    .skinUrl(skinUrl)
                    .storyUrl(storyUrl)
                    .user(savedUser)
                    .skinLocation(idx % 2 == 0 ? domestic : foreign)
                    .skinTime(idx % 2 == 0 ? year2020 : year2023)
                    .likeCount((long) (Math.random() * 100))
                    .viewCount((long) (Math.random() * 100))
                    .isPublic(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Integer size = 10;
        final Integer page = 0;
        final Pageable pageable = PageRequest.of(page, size, sortBy);
        final PostPointFilter postPointFilter = PostPointFilter.builder()
                .latitude(0.98)
                .longitude(0.98)
                .radius(3.0)
                .build();

        // when
        log.info("userId = {}", userId);
        final List<Post> posts = postRepository.findAllByUser(savedUser);
        log.info("post userId = {}", posts.get(0).getUser().getId());
        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postRepository.findAllUserPostByLocation(savedUser, postPointFilter, pageable);
        final String result = objectMapper.writeValueAsString(postListReadResponsePage);

        // then
        assertThat(postListReadResponsePage.getContent()).isNotNull();
        log.info("postListReadResponsePage = {}", result);
    }
}
