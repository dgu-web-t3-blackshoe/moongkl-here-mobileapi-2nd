package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.User;

import java.util.UUID;

public interface ViewService {
    PostDto.IncreaseViewCountDto increaseViewCount(UUID postId, User user);
}
