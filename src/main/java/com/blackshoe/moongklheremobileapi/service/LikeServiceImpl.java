package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Like;
import com.blackshoe.moongklheremobileapi.entity.LikePk;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.LikeRepository;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    private final PostRepository postRepository;

    public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Override
    public PostDto.LikePostDto likePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        final Like savedLike = likeRepository.save(like);

        post.increaseLikeCount();

        final PostDto.LikePostDto likePostDto = PostDto.LikePostDto.builder()
                .postId(post.getId())
                .likeCount(post.getLikeCount())
                .userId(user.getId())
                .createdAt(savedLike.getCreatedAt())
                .build();

         return likePostDto;
    }

    @Override
    public PostDto.LikePostDto dislikePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final LikePk likePk = LikePk.builder()
                .post(post)
                .user(user)
                .build();

        final Like like = likeRepository.findById(likePk).orElseThrow(() -> new PostException(PostErrorResult.LIKE_NOT_FOUND));

        likeRepository.delete(like);

        post.decreaseLikeCount();

        final PostDto.LikePostDto likePostDto = PostDto.LikePostDto.builder()
                .postId(post.getId())
                .likeCount(post.getLikeCount())
                .userId(user.getId())
                .deletedAt(LocalDateTime.now())
                .build();

        return likePostDto;
    }
}
