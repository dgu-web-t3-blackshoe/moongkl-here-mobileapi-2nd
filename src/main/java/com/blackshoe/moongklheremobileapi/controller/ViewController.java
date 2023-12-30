package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.ViewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/views")
public class ViewController {

    private final ViewService viewService;

    private final ObjectMapper objectMapper;

    public ViewController(ViewService viewService, ObjectMapper objectMapper) {
        this.viewService = viewService;
        this.objectMapper = objectMapper;

    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    public ResponseEntity<ResponseDto<PostDto.IncreaseViewCountDto>> increaseViewCount(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        final PostDto.IncreaseViewCountDto increaseViewCountDto = viewService.increaseViewCount(postId, user);

        final ResponseDto<PostDto.IncreaseViewCountDto> responseDto = ResponseDto.<PostDto.IncreaseViewCountDto>success()
                .payload(increaseViewCountDto)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
