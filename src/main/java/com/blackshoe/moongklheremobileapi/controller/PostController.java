package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.SkinUrlDto;
import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.PostService;
import com.blackshoe.moongklheremobileapi.service.SkinService;
import com.blackshoe.moongklheremobileapi.service.StoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final SkinService skinService;
    private final StoryService storyService;
    private final PostService postService;
    private final ObjectMapper objectMapper;

    public PostController(SkinService skinService, StoryService storyService, PostService postService, ObjectMapper objectMapper) {
        this.skinService = skinService;
        this.storyService = storyService;
        this.postService = postService;
        this.objectMapper = objectMapper;
    }


    @PostMapping
    public ResponseEntity<ResponseDto> createPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestPart(name = "skin") MultipartFile skin,
                                                  @RequestPart(name = "story") MultipartFile story,
                                                  @RequestPart(name = "post_create_request") @Valid
                                                      PostDto.PostCreateRequest postCreateRequest) {

        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        final SkinUrlDto skinUrlDto = skinService.uploadSkin(userId, skin);

        final StoryUrlDto storyUrlDto = storyService.uploadStory(userId, story);

        final PostDto postDto = postService.createPost(user, skinUrlDto, storyUrlDto, postCreateRequest);

        final PostDto.PostCreateResponse postCreateResponse = PostDto.PostCreateResponse.builder()
                .postId(postDto.getPostId().toString())
                .createdAt(postDto.getCreatedAt().toString())
                .build();

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postCreateResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ResponseDto> handlePostException(PostException e) {
        final PostErrorResult errorResult = e.getPostErrorResult();

        final ResponseDto responseDto = ResponseDto.builder()
                .error(errorResult.getMessage())
                .build();

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }
}
