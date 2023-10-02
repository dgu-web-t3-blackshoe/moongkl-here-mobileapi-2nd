package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void PostRepositoryIsNotNull() {
        assertThat(postRepository).isNotNull();
    }

    @Test
    public void PostUpload() {
        //given
        SkinUrl skinUrl = SkinUrl.builder()
                .build();

        StoryUrl storyUrl = StoryUrl.builder()
                .build();

        SkinTime skinTime = SkinTime.builder()
                .build();

        SkinLocation skinLocation = SkinLocation.builder()
                .build();

        User user = User.builder()
                .build();

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .user(user)
                .likeCount(0)
                .favoriteCount(0)
                .viewCount(0)
                .isPublic(true)
                .build();

        //when
        final Post savedPost = postRepository.save(post);

        //then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getSkinUrl()).isEqualTo(skinUrl);
        assertThat(savedPost.getStoryUrl()).isEqualTo(storyUrl);
        assertThat(savedPost.getSkinTime()).isEqualTo(skinTime);
        assertThat(savedPost.getSkinLocation()).isEqualTo(skinLocation);
        assertThat(savedPost.getUser()).isEqualTo(user);
        assertThat(savedPost.getLikeCount()).isEqualTo(0);
        assertThat(savedPost.getFavoriteCount()).isEqualTo(0);
        assertThat(savedPost.getViewCount()).isEqualTo(0);
        assertThat(savedPost.getIsPublic()).isEqualTo(true);
        assertThat(savedPost.getCreatedAt()).isNotNull();
    }
}
