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
import org.springframework.data.domain.Page;
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

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDto> getPost(@PathVariable("postId") UUID postId) {

        final PostDto.PostReadResponse postReadResponse = postService.getPost(postId);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postReadResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getPostList(@RequestParam(name = "from", required = false, defaultValue = "2001-01-01") String from,
                                                   @RequestParam(name = "to", required = false, defaultValue = "2999-12-31") String to,
                                                   @RequestParam(name = "location", required = false, defaultValue = "default") String location,
                                                   @RequestParam(name = "latitude", required = false, defaultValue = "0") Double latitude,
                                                   @RequestParam(name = "longitude", required = false, defaultValue = "0") Double longitude,
                                                   @RequestParam(name = "radius", required = false, defaultValue = "0") Double radius,
                                                   @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
                                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {

        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postListReadResponsePage, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
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
