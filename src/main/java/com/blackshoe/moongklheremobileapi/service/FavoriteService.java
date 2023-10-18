package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface FavoriteService {
    PostDto.FavoritePostDto favoritePost(UUID postId, User user);

    PostDto.FavoritePostDto deleteFavoritePost(UUID postId, User user);

    Page<PostDto.PostListReadResponse> getUserFavoritePostList(User user, Integer size, Integer page);
}
