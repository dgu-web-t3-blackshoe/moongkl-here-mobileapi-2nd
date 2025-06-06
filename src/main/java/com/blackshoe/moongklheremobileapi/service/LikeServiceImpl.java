package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Like;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.InteractionErrorResult;
import com.blackshoe.moongklheremobileapi.exception.InteractionException;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.LikeRepository;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    @Transactional
    public PostDto.LikePostDto likePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new InteractionException(InteractionErrorResult.USER_ALREADY_LIKED_POST);
        }

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
    @Transactional
    public PostDto.DislikePostDto dislikePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final Like like = likeRepository.findByPostAndUser(post, user).orElseThrow(() -> new InteractionException(InteractionErrorResult.LIKE_NOT_FOUND));

        likeRepository.delete(like);

        post.decreaseLikeCount();

        final PostDto.DislikePostDto dislikePostDto = PostDto.DislikePostDto.builder()
                .postId(post.getId())
                .likeCount(post.getLikeCount())
                .userId(user.getId())
                .deletedAt(LocalDateTime.now())
                .build();

        return dislikePostDto;
    }

    @Override
    public Page<PostDto.PostListReadResponse> getUserLikedPostList(User user, int size, int page) {

        final Pageable pageable = PageRequest.of(page, size);

        final Page<PostDto.PostListReadResponse> userLikedPostResponse
                = likeRepository.findAllLikedPostByUser(user, pageable);

        return userLikedPostResponse;
    }

    @Override
    public PostDto.DidUserLikedPostResponse didUserLikedPost(User user, UUID postId) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final Boolean didUserLikedPost = likeRepository.existsByPostAndUser(post, user);

        final PostDto.DidUserLikedPostResponse didUserLikedPostResponse = PostDto.DidUserLikedPostResponse.builder()
                .isTrue(didUserLikedPost)
                .build();

        return didUserLikedPostResponse;
    }
}
