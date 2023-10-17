package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    public void assert_isNotNull() {
        assertThat(favoriteRepository).isNotNull();
    }

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

    final UUID userId = UUID.randomUUID();

    final User user = User.builder()
            .id(userId)
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
    public void save_returns_isNotNull() {
        //given
        final Favorite favorite = Favorite.builder()
                .post(post)
                .user(user)
                .build();

        //when
        final Favorite savedFavorite = favoriteRepository.save(favorite);

        //then
        assertThat(savedFavorite).isNotNull();
        assertThat(savedFavorite.getPost()).isNotNull();
        assertThat(savedFavorite.getUser()).isNotNull();
    }

    @Test
    public void findById_returns_savedLike() {
        //given
        final Favorite favorite = Favorite.builder()
                .post(post)
                .user(user)
                .build();

        //when
        final Favorite savedFavorite = favoriteRepository.save(favorite);
        final FavoritePk favoritePk = savedFavorite.getFavoritePk();

        //when
        final Favorite foundFavorite = favoriteRepository.findById(favoritePk).get();

        //then
        assertThat(foundFavorite).isNotNull();
        assertThat(foundFavorite.getPost()).isNotNull();
        assertThat(foundFavorite.getUser()).isNotNull();
    }
}
