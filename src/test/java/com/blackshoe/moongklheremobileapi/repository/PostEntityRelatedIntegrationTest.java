package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PostEntityRelatedIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SkinUrlRepository skinUrlRepository;

    @Autowired
    private StoryUrlRepository storyUrlRepository;

    @Autowired
    private SkinLocationRepository skinLocationRepository;

    @Autowired
    private SkinTimeRepository skinTimeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    public void setUp() {
        final User user1 = User.builder()
                .email("user1")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        userRepository.save(user1);

        final User user2 = User.builder()
                .email("user2")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        userRepository.save(user2);

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

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .build();

        post.setUser(user1);

        postRepository.save(post);
    }

    @Test
    public void postDelete_whenSuccess_relatedAlsoDeleted() {
        //given
        final User foundUser = userRepository.findByEmail("user2").get();

        final Post foundPost = postRepository.findAll().get(0);

        final Like like = Like.builder()
                .post(foundPost)
                .user(foundUser)
                .build();

        final Like savedLike = likeRepository.save(like);

        final UUID postId = foundPost.getId();
        final UUID skinUrlId = foundPost.getSkinUrl().getId();
        final UUID storyUrlId = foundPost.getStoryUrl().getId();
        final UUID skinTimeId = foundPost.getSkinTime().getId();
        final UUID skinLocationId = foundPost.getSkinLocation().getId();

        //when
        likeRepository.deleteByPostAndUser(foundPost, foundUser);
        postRepository.delete(foundPost);

        //then
        assertThat(postRepository.findById(postId)).isEmpty();
        assertThat(skinUrlRepository.findById(skinUrlId)).isEmpty();
        assertThat(storyUrlRepository.findById(storyUrlId)).isEmpty();
        assertThat(skinTimeRepository.findById(skinTimeId)).isEmpty();
        assertThat(skinLocationRepository.findById(skinLocationId)).isEmpty();
        assertThat(userRepository.findByEmail("user1")).isNotEmpty();
        assertThat(likeRepository.findByPostAndUser(foundPost, foundUser)).isEmpty();
    }

    @Test
    public void userDelete_whenSuccess_relatedAlsoDeleted() {
        //given
        final User foundUser = userRepository.findByEmail("user1").get();

        final Post foundPost = postRepository.findAll().get(0);

        final UUID postId = foundPost.getId();
        final UUID skinUrlId = foundPost.getSkinUrl().getId();
        final UUID storyUrlId = foundPost.getStoryUrl().getId();
        final UUID skinTimeId = foundPost.getSkinTime().getId();
        final UUID skinLocationId = foundPost.getSkinLocation().getId();

        //when
        userRepository.delete(foundUser);

        //then
        assertThat(userRepository.findByEmail("test")).isEmpty();
        assertThat(postRepository.findById(postId)).isEmpty();
        assertThat(skinUrlRepository.findById(skinUrlId)).isEmpty();
        assertThat(storyUrlRepository.findById(storyUrlId)).isEmpty();
        assertThat(skinTimeRepository.findById(skinTimeId)).isEmpty();
        assertThat(skinLocationRepository.findById(skinLocationId)).isEmpty();
    }

    @Test
    public void userWithLikeDelete_whenSuccess_relatedAlsoDeleted() {
        //given
        final User foundUser1 = userRepository.findByEmail("user1").get();

        final User foundUser2 = userRepository.findByEmail("user2").get();

        final Post foundPost = postRepository.findAll().get(0);

        final UUID postId = foundPost.getId();
        final UUID skinUrlId = foundPost.getSkinUrl().getId();
        final UUID storyUrlId = foundPost.getStoryUrl().getId();
        final UUID skinTimeId = foundPost.getSkinTime().getId();
        final UUID skinLocationId = foundPost.getSkinLocation().getId();

        final Like like = Like.builder()
                .post(foundPost)
                .user(foundUser2)
                .build();

        final Like savedLike = likeRepository.save(like);

        //when
        likeRepository.deleteByPostAndUser(foundPost, foundUser2);

        userRepository.delete(foundUser1);


        //then
        assertThat(userRepository.findByEmail("test")).isEmpty();
        assertThat(postRepository.findById(postId)).isEmpty();
        assertThat(skinUrlRepository.findById(skinUrlId)).isEmpty();
        assertThat(storyUrlRepository.findById(storyUrlId)).isEmpty();
        assertThat(skinTimeRepository.findById(skinTimeId)).isEmpty();
        assertThat(skinLocationRepository.findById(skinLocationId)).isEmpty();
        assertThat(likeRepository.findByPostAndUser(foundPost, foundUser2)).isEmpty();
    }

    @Test
    public void userWithFavoriteDelete_whenSuccess_relatedAlsoDeleted() {
        //given
        final User foundUser1 = userRepository.findByEmail("user1").get();

        final User foundUser2 = userRepository.findByEmail("user2").get();

        final Post foundPost = postRepository.findAll().get(0);

        final UUID postId = foundPost.getId();
        final UUID skinUrlId = foundPost.getSkinUrl().getId();
        final UUID storyUrlId = foundPost.getStoryUrl().getId();
        final UUID skinTimeId = foundPost.getSkinTime().getId();
        final UUID skinLocationId = foundPost.getSkinLocation().getId();

        final Favorite favorite = Favorite.builder()
                .post(foundPost)
                .user(foundUser2)
                .build();

        final Favorite savedFavorite = favoriteRepository.save(favorite);

        //when
        favoriteRepository.deleteByPostAndUser(foundPost, foundUser2);

        userRepository.delete(foundUser1);

        //then
        assertThat(userRepository.findByEmail("test")).isEmpty();
        assertThat(postRepository.findById(postId)).isEmpty();
        assertThat(skinUrlRepository.findById(skinUrlId)).isEmpty();
        assertThat(storyUrlRepository.findById(storyUrlId)).isEmpty();
        assertThat(skinTimeRepository.findById(skinTimeId)).isEmpty();
        assertThat(skinLocationRepository.findById(skinLocationId)).isEmpty();
        assertThat(favoriteRepository.findByPostAndUser(foundPost, foundUser2)).isEmpty();
    }

    @Test
    public void findByPostAndUser_returns_savedLike() {
        //given
        final User foundUser1 = userRepository.findByEmail("user1").get();

        final Post foundPost = postRepository.findAll().get(0);

        final Like like = Like.builder()
                .post(foundPost)
                .user(foundUser1)
                .build();

        final Like savedLike = likeRepository.save(like);

        //when
        final Like foundLike = likeRepository.findByPostAndUser(foundPost, foundUser1).get();

        //then
        assertThat(foundLike).isNotNull();
        assertThat(foundLike.getPost()).isNotNull();
        assertThat(foundLike.getUser()).isNotNull();
    }

    @Test
    public void deleteLike_whenPostedUserLiked_noError() {
        //given
        final User foundUser1 = userRepository.findByEmail("user1").get();

        final Post foundPost = postRepository.findAll().get(0);

        final Like like = Like.builder()
                .post(foundPost)
                .user(foundUser1)
                .build();

        final Like savedLike = likeRepository.save(like);

        //when
        likeRepository.deleteByPostAndUser(foundPost, foundUser1);

        //then
        assertThat(likeRepository.existsByPostAndUser(foundPost, foundUser1)).isFalse();
    }

    @Test
    public void findByPostAndUser_returns_savedFavorite() {
        //given
        final User foundUser = userRepository.findByEmail("user1").get();

        final Post foundPost = postRepository.findAll().get(0);

        final Favorite favorite = Favorite.builder()
                .post(foundPost)
                .user(foundUser)
                .build();

        //when
        final Favorite savedFavorite = favoriteRepository.save(favorite);

        //when
        final Favorite foundFavorite = favoriteRepository.findByPostAndUser(foundPost, foundUser).get();

        //then
        assertThat(foundFavorite).isNotNull();
        assertThat(foundFavorite.getPost()).isNotNull();
        assertThat(foundFavorite.getUser()).isNotNull();
    }
}
