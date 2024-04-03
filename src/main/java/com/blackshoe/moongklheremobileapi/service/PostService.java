package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.SkinUrlDto;
import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.dto.TemporaryPostDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.UUID;

public interface PostService {
    PostDto createPost(User user,
                       SkinUrlDto uploadedSkinUrl,
                       StoryUrlDto uploadedStoryUrl,
                       PostDto.PostCreateRequest postCreateRequest);

    PostDto changePostIsPublic(User user, UUID postId, Boolean isPublic);

    PostDto.PostReadResponse getPost(UUID postId);

    Page<PostDto.PostListReadResponse> getPostList(String from, String to,
                                                   String location, Double latitude, Double longitude, Double radius,
                                                   String sort, Integer size, Integer page);

    Page<PostDto.PostGroupByCityReadResponse> getUserPostListGroupedByCity(User user,
                                                                           Double latitude,
                                                                           Double longitude,
                                                                           Double radius,
                                                                           Integer size, Integer page);

    Page<PostDto.PostListReadResponse> getUserCityPostList(User user, String country, String state, String city, String sort, Integer size, Integer page);

    Page<PostDto.PostListReadResponse> getUserSkinTimePostList(User user, String from, String to, String sort, Integer size, Integer page);

    PostDto saveTemporaryPost(User user, TemporaryPostDto.TemporaryPostToSave temporaryPostToSave, Boolean isPublic);

    void deletePost(UUID userId, UUID postId);

    @Transactional
    void deletePostRelationships(UUID userId, UUID postId);

    Page<PostDto.PostListReadResponse> getPublicUserPostList(UUID userId, String sort, Integer size, Integer page);

    Page<PostDto.PostListReadResponse> getAllUserPostList(User user, String sort, Integer size, Integer page);

    Page<PostDto.PostWithDateListReadResponse> getUserPostWithDateList(User user, Integer size, Integer page);

    void sharePost(UUID postId);

}
