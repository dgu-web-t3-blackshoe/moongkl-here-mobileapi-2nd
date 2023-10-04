package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.*;

public interface PostService {
    Post createPost(User user, SkinUrl skinUrl, StoryUrl storyUrl, PostDto.PostCreateRequest postCreateRequest);
}
