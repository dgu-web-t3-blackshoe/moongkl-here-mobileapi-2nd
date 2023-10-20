package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.SkinUrlDto;
import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.dto.TemporaryPostDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TemporaryPostService {
    TemporaryPostDto createTemporaryPost(User user,
                                         SkinUrlDto uploadedSkinUrl,
                                         StoryUrlDto uploadedStoryUrl,
                                         TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest);

    Page<TemporaryPostDto.TemporaryPostListReadResponse> getUserTemporaryPostList(User user, Integer size, Integer page);

    TemporaryPostDto getTemporaryPost(UUID temporaryPostId, User user);
}
