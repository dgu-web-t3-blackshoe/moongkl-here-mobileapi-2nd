package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.InteractionErrorResult;
import com.blackshoe.moongklheremobileapi.exception.InteractionException;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("likes")
public class LikeController {

    private final LikeService likeService;

    private final ObjectMapper objectMapper;

    public LikeController(LikeService likeService, ObjectMapper objectMapper) {
        this.likeService = likeService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}")
    public ResponseEntity<ResponseDto> likePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        final PostDto.LikePostDto likePostDto = likeService.likePost(postId, user);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(likePostDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDto> dislikePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        final PostDto.LikePostDto dislikePostDto = likeService.dislikePost(postId, user);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(dislikePostDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto> getUserLikedPostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                            @PathVariable UUID userId,
                                                            @RequestParam(defaultValue = "10") Integer size,
                                                            @RequestParam(defaultValue = "0") Integer page) {
        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new InteractionException(InteractionErrorResult.LIKE_USER_NOT_MATCH);
        }

        final Page<PostDto.PostListReadResponse> userLikedPostList =
                likeService.getUserLikedPostList(user, size, page);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userLikedPostList, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}/{postId}")
    public ResponseEntity<ResponseDto> didUserLikedPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @PathVariable UUID userId,
                                                        @PathVariable UUID postId) {
        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new InteractionException(InteractionErrorResult.LIKE_USER_NOT_MATCH);
        }

        final PostDto.DidUserLikedPostResponse didUserLikedPostResponse =
                likeService.didUserLikedPost(user, postId);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(didUserLikedPostResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
