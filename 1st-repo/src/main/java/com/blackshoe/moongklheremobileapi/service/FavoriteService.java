package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface FavoriteService {
    PostDto.FavoritePostDto favoritePost(UUID postId, User user);

    PostDto.DeleteFavoritePostDto deleteFavoritePost(UUID postId, User user);

    Page<PostDto.PostListReadResponse> getUserFavoritePostList(User user, Integer size, Integer page);

    PostDto.DidUserFavoritePostResponse didUserFavoritePost(User user, UUID postId);

    Page<PostDto.PostGroupByCityReadResponse> getUserFavoritePostListGroupedByCity(User user, Double latitude, Double longitude, Double radius, Integer size, Integer page);

    Page<PostDto.PostListReadResponse> getUserCityFavoritePostList(User user, String country, String state, String city, String sort, Integer size, Integer page);

    Page<PostDto.PostWithDateListReadResponse> getUserFavoritePostWithDateList(User user, Integer size, Integer page);
}
