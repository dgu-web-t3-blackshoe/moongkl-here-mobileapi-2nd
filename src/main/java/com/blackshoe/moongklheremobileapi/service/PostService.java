package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.SkinUrlDto;
import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.entity.*;

import java.util.UUID;

public interface PostService {
    PostDto createPost(User user,
                       SkinUrlDto uploadedSkinUrl,
                       StoryUrlDto uploadedStoryUrl,
                       PostDto.PostCreateRequest postCreateRequest);

    PostDto.PostReadResponse getPost(UUID postId);
}
