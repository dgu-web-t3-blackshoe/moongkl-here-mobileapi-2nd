package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
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
        SkinUrl skinUrl = SkinUrl.builder()
                .build();

//        StoryUrl storyUrl = StoryUrl.builder()
//                .build();
//
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
//                .storyUrl(storyUrl)
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
//        assertThat(savedPost.getStoryUrl()).isEqualTo(storyUrl);
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
        SkinUrl skinUrl = SkinUrl.builder()
                .build();

        Post post = Post.builder()
                .skinUrl(skinUrl)
                .likeCount(10)
                .favoriteCount(100)
                .viewCount(20)
                .isPublic(true)
                .build();

        postRepository.save(post);

        //when
        Post findedPost = postRepository.findById(post.getId()).orElse(null);

        //then
        assertThat(findedPost).isNotNull();
        assertThat(findedPost.getId()).isEqualTo(post.getId());
        assertThat(findedPost.getSkinUrl()).isEqualTo(skinUrl);
        assertThat(findedPost.getLikeCount()).isEqualTo(10);
        assertThat(findedPost.getFavoriteCount()).isEqualTo(100);
        assertThat(findedPost.getViewCount()).isEqualTo(20);
        assertThat(findedPost.isPublic()).isEqualTo(true);
        assertThat(findedPost.getCreatedAt()).isNotNull();
    }
}
