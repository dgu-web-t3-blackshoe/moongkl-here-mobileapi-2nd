package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.User;

import java.util.UUID;

public interface LikeService {
    PostDto.LikePostDto likePost(UUID postId, User user);

    PostDto.LikePostDto dislikePost(UUID postId, User user);
}
