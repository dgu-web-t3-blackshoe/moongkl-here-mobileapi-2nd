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
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto<PostDto.PostCreateResponse>> createPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                              @RequestPart(name = "skin") MultipartFile skin,
                                                                              @RequestPart(name = "story") MultipartFile story,
                                                                              @RequestPart(name = "post_create_request") @Valid
                                                                              PostDto.PostCreateRequest postCreateRequest) {
        //log all
        log.info("create post, postCreateRequest: {}", postCreateRequest);

        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        final SkinUrlDto skinUrlDto = skinService.uploadSkin(userId, skin);

        final StoryUrlDto storyUrlDto = storyService.uploadStory(userId, story);

        final PostDto postDto = postService.createPost(user, skinUrlDto, storyUrlDto, postCreateRequest);

        final PostDto.PostCreateResponse postCreateResponse = PostDto.PostCreateResponse.builder()
                .postId(postDto.getPostId().toString())
                .createdAt(postDto.getCreatedAt())
                .build();

        final ResponseDto<PostDto.PostCreateResponse> responseDto = ResponseDto.<PostDto.PostCreateResponse>success()
                .payload(postCreateResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}/is-public")
    public ResponseEntity<ResponseDto<PostDto.PostUpdateResponse>> changePostIsPublic(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                      @PathVariable("postId") UUID postId,
                                                                                      @RequestBody @Valid PostDto.PostIsPublicChangeRequest postIsPublicChangeRequest) {

        final Boolean newIsPublic = Boolean.valueOf(postIsPublicChangeRequest.getIsPublic());

        final User user = userPrincipal.getUser();

        final PostDto postDto = postService.changePostIsPublic(user, postId, newIsPublic);

        final PostDto.PostUpdateResponse postUpdateResponse = PostDto.PostUpdateResponse.builder()
                .postId(postDto.getPostId().toString())
                .updatedAt(postDto.getUpdatedAt())
                .build();

        final ResponseDto<PostDto.PostUpdateResponse> responseDto = ResponseDto.<PostDto.PostUpdateResponse>success()
                .payload(postUpdateResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDto<PostDto.PostReadResponse>> getPost(@PathVariable("postId") UUID postId) {
        final PostDto.PostReadResponse postReadResponse = postService.getPost(postId);

        final ResponseDto<PostDto.PostReadResponse> responseDto = ResponseDto.<PostDto.PostReadResponse>success()
                .payload(postReadResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping()
    public ResponseEntity<ResponseDto<Page<PostDto.PostListReadResponse>>> getPostList(@RequestParam(name = "user", required = false, defaultValue = "false") String user,
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

        final ResponseDto<Page<PostDto.PostListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostListReadResponse>>success()
                .payload(postListReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(params = {"user", "public"})
    public ResponseEntity<ResponseDto<Page<PostDto.PostListReadResponse>>> getPublicUserPostList(@RequestParam(name = "user") UUID userId,
                                                                                                 @RequestParam(name = "public") Boolean isPublic,
                                                                                                 @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
                                                                                                 @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                                                                 @RequestParam(name = "page", required = false, defaultValue = "0") Integer page
    ) {

        log.info("get public user post list, user: {}, public: {}, sort: {}, size: {}, page: {}",
                userId, isPublic, sort, size, page);

        if (!isPublic) {
            throw new PostException(PostErrorResult.INVALID_PARAMETER_FOR_GET_PUBLIC_USER_POST_LIST);
        }

        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getPublicUserPostList(userId, sort, size, page);

        final ResponseDto<Page<PostDto.PostListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostListReadResponse>>success()
                .payload(postListReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user"})
    public ResponseEntity<ResponseDto<Page<PostDto.PostListReadResponse>>> getAllUserPostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                              @RequestParam(name = "user") UUID userId,
                                                                                              @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
                                                                                              @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                                                              @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {

        log.info("get all user post list, user: {}, sort: {}, size: {}, page: {}",
                userId, sort, size, page);

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = postService.getAllUserPostList(user, sort, size, page);

        final ResponseDto<Page<PostDto.PostListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostListReadResponse>>success()
                .payload(postListReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user", "latitude", "longitude", "radius"})
    public ResponseEntity<ResponseDto<Page<PostDto.PostGroupByCityReadResponse>>> getUserPostListGroupedByCity(@AuthenticationPrincipal UserPrincipal userPrincipal,
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

        final ResponseDto<Page<PostDto.PostGroupByCityReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostGroupByCityReadResponse>>success()
                .payload(postGroupByCityReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user", "country", "state", "city"})
    public ResponseEntity<ResponseDto<Page<PostDto.PostListReadResponse>>> getUserCityPostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
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

        final ResponseDto<Page<PostDto.PostListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostListReadResponse>>success()
                .payload(postListReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user", "from", "to"})
    public ResponseEntity<ResponseDto<Page<PostDto.PostListReadResponse>>> getUserSkinTimePostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
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

        final ResponseDto<Page<PostDto.PostListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostListReadResponse>>success()
                .payload(postListReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/by-temporary")
    public ResponseEntity<ResponseDto<PostDto.PostCreateResponse>> saveTemporaryPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                     @RequestParam(name = "save-temporary-post") Boolean saveTemporaryPost,
                                                                                     @RequestBody @Valid PostDto.SaveTemporaryPostRequest saveTemporaryPostRequest) throws JsonProcessingException {

        log.info("save temporary post, save-temporary-post: {}, saveTemporaryPostRequest: {}", saveTemporaryPost, objectMapper.writeValueAsString(saveTemporaryPostRequest));

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
                .createdAt(postDto.getCreatedAt())
                .build();

        final ResponseDto<PostDto.PostCreateResponse> responseDto = ResponseDto.<PostDto.PostCreateResponse>success()
                .payload(postCreateResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDto<PostDto.DeletePostResponse>> deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                              @PathVariable("postId") UUID postId) {

        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        postService.deletePost(userId, postId);

        final PostDto.DeletePostResponse deletePostResponse = PostDto.DeletePostResponse.builder()
                .postId(postId)
                .deletedAt(LocalDateTime.now())
                .build();

        final ResponseDto<PostDto.DeletePostResponse> responseDto = ResponseDto.<PostDto.DeletePostResponse>success()
                .payload(deletePostResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user", "with-date"})
    public ResponseEntity<ResponseDto<Page<PostDto.PostWithDateListReadResponse>>> getUserPostWithDateList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                                           @RequestParam("user") UUID userId,
                                                                                                           @RequestParam("with-date") boolean withDate,
                                                                                                           @RequestParam(defaultValue = "10") Integer size,
                                                                                                           @RequestParam(defaultValue = "0") Integer page) {
        if (!withDate) {
            throw new PostException(PostErrorResult.INVALID_PARAMETER_FOR_GET_POST_WITH_DATE_LIST);
        }

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        final Page<PostDto.PostWithDateListReadResponse> postListWithDateReadResponsePage
                = postService.getUserPostWithDateList(user, size, page);

        final ResponseDto<Page<PostDto.PostWithDateListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostWithDateListReadResponse>>success()
                .payload(postListWithDateReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/share")
    public ResponseEntity<ResponseDto> sharePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                     @RequestParam("post_id") UUID postId) {
        postService.sharePost(postId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/use")
    public ResponseEntity<ResponseDto> usePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @RequestParam("post_id") UUID postId) {
        postService.usePost(postId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/view")
    public ResponseEntity<ResponseDto> viewPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @RequestParam("post_id") UUID postId) {
        postService.viewPost(postId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/story")
    public ResponseEntity<ResponseDto<Page<PostDto.EnterpriseStoryReadResponse>>> getEnterpriseStory(@RequestParam(defaultValue = "10") Integer size,
                                                                                                      @RequestParam(defaultValue = "0") Integer page) {
        final Page<PostDto.EnterpriseStoryReadResponse> enterpriseStoryReadResponsePage
                = storyService.getEnterpriseStory(size, page);

        final ResponseDto<Page<PostDto.EnterpriseStoryReadResponse>> responseDto = ResponseDto.<Page<PostDto.EnterpriseStoryReadResponse>>success()
                .payload(enterpriseStoryReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
