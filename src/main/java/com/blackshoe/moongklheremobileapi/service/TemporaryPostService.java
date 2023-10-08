package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.SkinUrlDto;
import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.dto.TemporaryPostDto;
import com.blackshoe.moongklheremobileapi.entity.User;

import java.util.UUID;

public interface TemporaryPostService {
    TemporaryPostDto createTemporaryPost(User user,
                                         SkinUrlDto uploadedSkinUrl,
                                         StoryUrlDto uploadedStoryUrl,
                                         TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest);
}
