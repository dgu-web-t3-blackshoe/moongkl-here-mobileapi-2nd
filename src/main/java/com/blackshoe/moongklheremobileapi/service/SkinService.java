package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.SkinUrlDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SkinService {
    SkinUrlDto uploadSkin(UUID userId, MultipartFile skin);
}
