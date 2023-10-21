package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.PostService;
import com.blackshoe.moongklheremobileapi.service.SkinService;
import com.blackshoe.moongklheremobileapi.service.StoryService;
import com.blackshoe.moongklheremobileapi.service.TemporaryPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {

    private final SkinService skinService;
    private final StoryService storyService;
    private final PostService postService;
    private final TemporaryPostService temporaryPostService;
    private final ObjectMapper objectMapper;

    public PostController(SkinService skinService,
                          StoryService storyService,
                          PostService postService,
                          TemporaryPostService temporaryPostService,
                          ObjectMapper objectMapper) {
        this.skinService = skinService;
        this.storyService = storyService;
        this.postService = postService;
        this.temporaryPostService = temporaryPostService;
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

    @PutMapping("/{postId}/is-public")
    public ResponseEntity<ResponseDto> changePostIsPublic(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @PathVariable("postId") UUID postId,
                                                          @RequestBody @Valid PostDto.PostIsPublicChangeRequest postIsPublicChangeRequest) {

        final Boolean newIsPublic = Boolean.valueOf(postIsPublicChangeRequest.getIsPublic());

        final User user = userPrincipal.getUser();

        final PostDto postDto = postService.changePostIsPublic(user, postId, newIsPublic);

        final PostDto.PostUpdateResponse postUpdateResponse = PostDto.PostUpdateResponse.builder()
                .postId(postDto.getPostId().toString())
                .updatedAt(postDto.getUpdatedAt().toString())
                .build();

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postUpdateResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
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
    public ResponseEntity<ResponseDto> getPostList(@RequestParam(name = "user", required = false, defaultValue = "false") String user,
                                                   @RequestParam(name = "from", required = false, defaultValue = "2001-01-01") String from,
                                                   @RequestParam(name = "to", required = false, defaultValue = "2999-12-31") String to,
                                                   @RequestParam(name = "location", required = false, defaultValue = "default") String location,
                                                   @RequestParam(name = "latitude", required = false, defaultValue = "0") Double latitude,
                                                   @RequestParam(name = "longitude", required = false, defaultValue = "0") Double longitude,
                                                   @RequestParam(name = "radius", required = false, defaultValue = "0") Double radius,
                                                   @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
                                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        if (!user.equals("false")) {
            throw new PostException(PostErrorResult.INVALID_PARAMETER_FOR_GET_POST_LIST);
        }

        log.info("get post list from: {}, to: {}, location: {}, latitude: {}, longitude: {}, radius: {}, sort: {}, size: {}, page: {}",
                from, to, location, latitude, longitude, radius, sort, size, page);

        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getPostList(from, to, location, latitude, longitude, radius, sort, size, page);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postListReadResponsePage, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(params = {"user", "latitude", "longitude", "radius"})
    public ResponseEntity<ResponseDto> getUserPostListGroupedByCity(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                    @RequestParam(name = "user") UUID userId,
                                                                    @RequestParam(name = "latitude") Double latitude,
                                                                    @RequestParam(name = "longitude") Double longitude,
                                                                    @RequestParam(name = "radius") Double radius,
                                                                    @RequestParam(name = "size", required = false, defaultValue = "50") Integer size,
                                                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {

        log.info("get user post list grouped by city, user: {}, latitude: {}, longitude: {}, radius: {}, size: {}, page: {}",
                userId, latitude, longitude, radius, size, page);

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        final Page<PostDto.PostGroupByCityReadResponse> postGroupByCityReadResponsePage
                = postService.getUserPostListGroupedByCity(user, latitude, longitude, radius, size, page);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postGroupByCityReadResponsePage, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(params = {"user", "country", "state", "city"})
    public ResponseEntity<ResponseDto> getUserCityPostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @RequestParam(name = "user") UUID userId,
                                                           @RequestParam(name = "country") String country,
                                                           @RequestParam(name = "state") String state,
                                                           @RequestParam(name = "city") String city,
                                                           @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
                                                           @RequestParam(name = "size", required = false, defaultValue = "50") Integer size,
                                                           @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        log.info("get user city post list, user: {}, country: {}, state: {}, city: {}, sort: {}, size: {}, page: {}",
                userId, country, state, city, sort, size, page);

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getUserCityPostList(user, country, state, city, sort, size, page);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postListReadResponsePage, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(params = {"user", "from", "to"})
    public ResponseEntity<ResponseDto> getUserSkinTimePostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                               @RequestParam(name = "user") UUID userId,
                                                               @RequestParam(name = "from") String from,
                                                               @RequestParam(name = "to") String to,
                                                               @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
                                                               @RequestParam(name = "size", required = false, defaultValue = "50") Integer size,
                                                               @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {

        log.info("get user skin time post list, user: {}, from: {}, to: {}, sort: {}, size: {}, page: {}",
                userId, from, to, sort, size, page);

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getUserSkinTimePostList(user, from, to, sort, size, page);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postListReadResponsePage, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(params = {"save-temporary-post"})
    public ResponseEntity<ResponseDto> saveTemporaryPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @RequestParam(name = "save-temporary-post") Boolean saveTemporaryPost,
                                                         @RequestBody @Valid PostDto.SaveTemporaryPostRequest saveTemporaryPostRequest) throws JsonProcessingException {

        if (!saveTemporaryPost) {
            throw new PostException(PostErrorResult.INVALID_PARAMETER_VALUE_FOR_SAVE_TEMPORARY_POST);
        }

        log.info("save temporary post, save-temporary-post: {}, saveTemporaryPostRequest: {}", saveTemporaryPost, objectMapper.writeValueAsString(saveTemporaryPostRequest));

        final User user = userPrincipal.getUser();

        final UUID temporaryPostId = saveTemporaryPostRequest.getTemporaryPostId();

        final TemporaryPostDto.TemporaryPostToSave temporaryPostToSave = temporaryPostService.getAndDeleteTemporaryPostToSave(temporaryPostId, user);

        final Boolean isPublic = Boolean.valueOf(saveTemporaryPostRequest.getIsPublic());

        final PostDto postDto = postService.saveTemporaryPost(user, temporaryPostToSave, isPublic);

        final PostDto.PostCreateResponse postCreateResponse = PostDto.PostCreateResponse.builder()
                .postId(postDto.getPostId().toString())
                .createdAt(postDto.getCreatedAt().toString())
                .build();

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(postCreateResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDto> deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @PathVariable("postId") UUID postId) {

        final User user = userPrincipal.getUser();

        postService.deletePost(user, postId);

        final PostDto.DeletePostResponse deletePostResponse = PostDto.DeletePostResponse.builder()
                .postId(postId)
                .deletedAt(LocalDateTime.now().toString())
                .build();

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(deletePostResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }
}
