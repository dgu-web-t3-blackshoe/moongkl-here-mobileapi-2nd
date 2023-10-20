package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Favorite;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.InteractionErrorResult;
import com.blackshoe.moongklheremobileapi.exception.InteractionException;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.FavoriteRepository;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final PostRepository postRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, PostRepository postRepository) {
        this.favoriteRepository = favoriteRepository;
        this.postRepository = postRepository;
    }

    @Override
    public PostDto.FavoritePostDto favoritePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        if (favoriteRepository.existsByPostAndUser(post, user)) {
            throw new InteractionException(InteractionErrorResult.FAVORITE_ALREADY_EXIST);
        }

        final Favorite favorite = Favorite.builder()
                .post(post)
                .user(user)
                .build();

        final Favorite savedFavorite = favoriteRepository.save(favorite);

        post.increaseFavoriteCount();

        final PostDto.FavoritePostDto favoritePostDto = PostDto.FavoritePostDto.builder()
                .postId(post.getId())
                .favoriteCount(post.getFavoriteCount())
                .userId(user.getId())
                .createdAt(savedFavorite.getCreatedAt())
                .build();

        return favoritePostDto;
    }

    @Override
    public PostDto.FavoritePostDto deleteFavoritePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final Favorite favorite = favoriteRepository.findByPostAndUser(post, user).orElseThrow(() -> new InteractionException(InteractionErrorResult.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);

        post.decreaseFavoriteCount();

        final PostDto.FavoritePostDto favoritePostDto = PostDto.FavoritePostDto.builder()
                .postId(post.getId())
                .favoriteCount(post.getFavoriteCount())
                .userId(user.getId())
                .deletedAt(LocalDateTime.now())
                .build();

        return favoritePostDto;
    }

    @Override
    public Page<PostDto.PostListReadResponse> getUserFavoritePostList(User user, Integer size, Integer page) {

        final Pageable pageable = PageRequest.of(page, size);

        final Page<PostDto.PostListReadResponse> userFavoritePostResponse
                = favoriteRepository.findAllFavoritePostByUser(user, pageable);

        return userFavoritePostResponse;
    }
}
