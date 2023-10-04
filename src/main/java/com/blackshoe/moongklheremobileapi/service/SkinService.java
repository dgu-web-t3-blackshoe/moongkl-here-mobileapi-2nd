package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SkinService {
    SkinUrl uploadSkin(UUID userId, MultipartFile skin);
}
