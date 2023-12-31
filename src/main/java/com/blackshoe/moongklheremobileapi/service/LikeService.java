package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface LikeService {
    PostDto.LikePostDto likePost(UUID postId, User user);

    PostDto.DislikePostDto dislikePost(UUID postId, User user);

    Page<PostDto.PostListReadResponse> getUserLikedPostList(User user, int size, int page);

    PostDto.DidUserLikedPostResponse didUserLikedPost(User user, UUID postId);
}
