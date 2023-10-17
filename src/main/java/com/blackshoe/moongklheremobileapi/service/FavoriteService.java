package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.User;

import java.util.UUID;

public interface FavoriteService {
    PostDto.FavoritePostDto favoritePost(UUID postId, User user);

    PostDto.FavoritePostDto deleteFavoritePost(UUID postId, User user);
}
