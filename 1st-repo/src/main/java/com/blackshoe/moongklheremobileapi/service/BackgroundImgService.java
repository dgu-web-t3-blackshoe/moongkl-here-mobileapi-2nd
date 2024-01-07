package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.BackgroundImgUrlDto;
import com.blackshoe.moongklheremobileapi.dto.ProfileImgUrlDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface BackgroundImgService {
    BackgroundImgUrlDto uploadBackgroundImg(UUID userId, MultipartFile backgroundImg);
    void deleteBackgroundImg(UUID backgroundImgS3Url);

    BackgroundImgUrlDto getUserPresentBackgroundImgUrlDto(UUID userId);
}
