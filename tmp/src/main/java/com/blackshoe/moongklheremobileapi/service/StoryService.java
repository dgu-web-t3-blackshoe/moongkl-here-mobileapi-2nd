package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StoryService {
    StoryUrlDto uploadStory(UUID userId, MultipartFile story);

    void deleteStory(String storyS3Url);
}
