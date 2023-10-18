package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class InteractionEntityRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private LikeRepository likeRepository;

    private final Logger log = LoggerFactory.getLogger(InteractionEntityRepositoryIntegrationTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

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

//    final UUID userId = UUID.randomUUID();

    final User user = User.builder()
//            .id(userId)
            .email("test")
            .password("test")
            .nickname("test")
            .phoneNumber("test")
            .build();

    final UUID postId = UUID.randomUUID();

    final Post post = Post.builder()
            .id(postId)
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

    @Test
    public void findByPostAndUserInViewRepository_returns_isNotNull() {

        //given
        final User savedUser = userRepository.save(user);
        final Post savedPost = postRepository.save(post);

        final View view = View.builder()
                .post(savedPost)
                .user(savedUser)
                .build();

        final View savedView = viewRepository.save(view);

        //when
        final View foundView = viewRepository.findByPostAndUser(savedPost, savedUser).orElse(null);

        //then
        assertThat(foundView).isNotNull();
        assertThat(foundView.getPost()).isNotNull();
        assertThat(foundView.getUser()).isNotNull();
    }

    @Test
    public void findAllFavoritePostByUserInFavoriteRepository_isNotNull() throws JsonProcessingException {

        //given
        final User savedUser = userRepository.save(user);

        for (int idx = 0; idx < 20; idx++) {
            final Post postToBeSaved = Post.builder()
                    .skinUrl(skinUrl)
                    .storyUrl(storyUrl)
                    .skinTime(skinTime)
                    .skinLocation(skinLocation)
                    .user(user)
                    .likeCount(10)
                    .favoriteCount(100)
                    .viewCount(20)
                    .isPublic(true)
                    .build();

            final Post savedPost = postRepository.save(postToBeSaved);

            final Favorite favorite = Favorite.builder()
                    .post(savedPost)
                    .user(savedUser)
                    .build();

            final Favorite savedFavorite = favoriteRepository.save(favorite);
        }

        final Integer page = 0;
        final Integer size = 10;
        final Pageable pageable = PageRequest.of(page, size);

        //when
        final Page<PostDto.PostListReadResponse> foundFavoritePostPage = favoriteRepository.findAllFavoritePostByUser(savedUser, pageable);

        //then
        assertThat(foundFavoritePostPage.getContent()).isNotNull();
        log.info("foundViewPage: {}", objectMapper.writeValueAsString(foundFavoritePostPage));
    }

    @Test
    public void findAllByLikedPostByUserInLikeRepository_whenSuccess_isNotNull() throws JsonProcessingException {

        //given
        final User savedUser = userRepository.save(user);

        for (int idx = 0; idx < 20; idx++) {
            final Post postToBeSaved = Post.builder()
                    .skinUrl(skinUrl)
                    .storyUrl(storyUrl)
                    .skinTime(skinTime)
                    .skinLocation(skinLocation)
                    .user(user)
                    .likeCount(10)
                    .favoriteCount(100)
                    .viewCount(20)
                    .isPublic(true)
                    .build();

            final Post savedPost = postRepository.save(postToBeSaved);

            final Like like = Like.builder()
                    .post(savedPost)
                    .user(savedUser)
                    .build();

            final Like savedLike = likeRepository.save(like);
        }

        final Integer page = 0;
        final Integer size = 10;
        final Pageable pageable = PageRequest.of(page, size);

        //when
        final Page<PostDto.PostListReadResponse> foundLikedPostPage = likeRepository.findAllLikedPostByUser(savedUser, pageable);

        //then
        assertThat(foundLikedPostPage.getContent()).isNotNull();
        log.info("foundViewPage: {}", objectMapper.writeValueAsString(foundLikedPostPage));
    }
}
