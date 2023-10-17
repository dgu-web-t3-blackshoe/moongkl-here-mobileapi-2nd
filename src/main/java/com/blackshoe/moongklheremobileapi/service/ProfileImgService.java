package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.ProfileImgUrlDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProfileImgService {
    ProfileImgUrlDto uploadProfileImg(UUID userId, MultipartFile profileImg);
}
