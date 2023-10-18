package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.FavoriteService;
import com.blackshoe.moongklheremobileapi.service.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    private final ObjectMapper objectMapper;

    public FavoriteController(FavoriteService favoriteService, ObjectMapper objectMapper) {
        this.favoriteService = favoriteService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<ResponseDto> favoritePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        final PostDto.FavoritePostDto favoritePostDto = favoriteService.favoritePost(postId, user);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(favoritePostDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDto> deleteFavoritePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        final PostDto.FavoritePostDto deleteFavoritePostDto = favoriteService.deleteFavoritePost(postId, user);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(deleteFavoritePostDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }
}
