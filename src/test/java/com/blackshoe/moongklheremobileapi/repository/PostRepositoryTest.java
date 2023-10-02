package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

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
    public void PostSave() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .build();

//        SkinTime skinTime = SkinTime.builder()
//                .build();
//
//        SkinLocation skinLocation = SkinLocation.builder()
//                .build();
//
//        User user = User.builder()
//                .build();

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .storyUrl(storyUrl)
//                .skinTime(skinTime)
//                .skinLocation(skinLocation)
//                .user(user)
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
        assertThat(savedPost.getSkinUrl()).isEqualTo(skinUrl);
        assertThat(savedPost.getStoryUrl()).isEqualTo(storyUrl);
//        assertThat(savedPost.getSkinTime()).isEqualTo(skinTime);
//        assertThat(savedPost.getSkinLocation()).isEqualTo(skinLocation);
//        assertThat(savedPost.getUser()).isEqualTo(user);
        assertThat(savedPost.getLikeCount()).isEqualTo(10);
        assertThat(savedPost.getFavoriteCount()).isEqualTo(100);
        assertThat(savedPost.getViewCount()).isEqualTo(20);
        assertThat(savedPost.isPublic()).isEqualTo(true);
        assertThat(savedPost.getCreatedAt()).isNotNull();
    }

    @Test
    public void PostFindById() {
        //given
        final SkinUrl skinUrl = SkinUrl.builder()
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .build();

        final Post post = Post.builder()
                .skinUrl(skinUrl)
                .storyUrl(StoryUrl.builder().build())
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
        assertThat(foundPost.getLikeCount()).isEqualTo(10);
        assertThat(foundPost.getFavoriteCount()).isEqualTo(100);
        assertThat(foundPost.getViewCount()).isEqualTo(20);
        assertThat(foundPost.isPublic()).isEqualTo(true);
        assertThat(foundPost.getCreatedAt()).isNotNull();
    }
}
