package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.*;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.FavoriteService;
import com.blackshoe.moongklheremobileapi.service.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    private final ObjectMapper objectMapper;

    public FavoriteController(FavoriteService favoriteService, ObjectMapper objectMapper) {
        this.favoriteService = favoriteService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}")
    public ResponseEntity<ResponseDto<PostDto.FavoritePostDto>> favoritePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        final PostDto.FavoritePostDto favoritePostDto = favoriteService.favoritePost(postId, user);

        final ResponseDto<PostDto.FavoritePostDto> responseDto = ResponseDto.<PostDto.FavoritePostDto>success()
                .payload(favoritePostDto)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDto<PostDto.DeleteFavoritePostDto>> deleteFavoritePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        final PostDto.DeleteFavoritePostDto deleteFavoritePostDto = favoriteService.deleteFavoritePost(postId, user);

        final ResponseDto<PostDto.DeleteFavoritePostDto> responseDto = ResponseDto.<PostDto.DeleteFavoritePostDto>success()
                .payload(deleteFavoritePostDto)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<Page<PostDto.PostListReadResponse>>> getUserFavoritePostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                               @PathVariable UUID userId,
                                                               @RequestParam(defaultValue = "10") Integer size,
                                                               @RequestParam(defaultValue = "0") Integer page) {

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new InteractionException(InteractionErrorResult.FAVORITE_USER_NOT_MATCH);
        }

        final Page<PostDto.PostListReadResponse> userFavoritePostList =
                favoriteService.getUserFavoritePostList(user, size, page);

        final ResponseDto<Page<PostDto.PostListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostListReadResponse>>success()
                .payload(userFavoritePostList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}/{postId}")
    public ResponseEntity<ResponseDto<PostDto.DidUserFavoritePostResponse>> didUserFavoritePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable UUID userId,
                                                           @PathVariable UUID postId) {

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new InteractionException(InteractionErrorResult.FAVORITE_USER_NOT_MATCH);
        }

        final PostDto.DidUserFavoritePostResponse didUserFavoritePostResponse =
                favoriteService.didUserFavoritePost(user, postId);

        final ResponseDto<PostDto.DidUserFavoritePostResponse> responseDto = ResponseDto.<PostDto.DidUserFavoritePostResponse>success()
                .payload(didUserFavoritePostResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user", "latitude", "longitude", "radius"})
    @ApiOperation(value = "위치별 사용자 찜한 게시물 조회")
    public ResponseEntity<ResponseDto<Page<PostDto.PostGroupByCityReadResponse>>> getUserFavoritePostListGroupedByCity(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                                                       @RequestParam(name = "user") UUID userId,
                                                                                                                       @RequestParam(name = "latitude") Double latitude,
                                                                                                                       @RequestParam(name = "longitude") Double longitude,
                                                                                                                       @RequestParam(name = "radius") Double radius,
                                                                                                                       @RequestParam(name = "size", required = false, defaultValue = "50") Integer size,
                                                                                                                       @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {

        log.info("get user favorite post list grouped by city, user: {}, latitude: {}, longitude: {}, radius: {}, size: {}, page: {}",
                userId, latitude, longitude, radius, size, page);

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        final Page<PostDto.PostGroupByCityReadResponse> postGroupByCityReadResponsePage
                = favoriteService.getUserFavoritePostListGroupedByCity(user, latitude, longitude, radius, size, page);

        final ResponseDto<Page<PostDto.PostGroupByCityReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostGroupByCityReadResponse>>success()
                .payload(postGroupByCityReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user", "country", "state", "city"})
    @ApiOperation(value = "도시별 사용자 찜한 게시물 조회")
    public ResponseEntity<ResponseDto<Page<PostDto.PostListReadResponse>>> getUserCityFavoritePostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                                       @RequestParam(name = "user") UUID userId,
                                                                                                       @RequestParam(name = "country") String country,
                                                                                                       @RequestParam(name = "state") String state,
                                                                                                       @RequestParam(name = "city") String city,
                                                                                                       @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
                                                                                                       @RequestParam(name = "size", required = false, defaultValue = "50") Integer size,
                                                                                                       @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {

        log.info("get user city favorite post list, user: {}, country: {}, state: {}, city: {}, sort: {}, size: {}, page: {}",
                userId, country, state, city, sort, size, page);

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new PostException(PostErrorResult.USER_NOT_MATCH);
        }

        final Page<PostDto.PostListReadResponse> postListReadResponsePage
                = favoriteService.getUserCityFavoritePostList(user, country, state, city, sort, size, page);

        final ResponseDto<Page<PostDto.PostListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostListReadResponse>>success()
                .payload(postListReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"user", "with-date"})
    @ApiOperation(value = "날짜별 사용자 찜한 게시물 조회")
    public ResponseEntity<ResponseDto<Page<PostDto.PostWithDateListReadResponse>>> getUserFavoritePostWithDateList(@AuthenticationPrincipal UserPrincipal userPrincipal,
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
                = favoriteService.getUserFavoritePostWithDateList(user, size, page);

        final ResponseDto<Page<PostDto.PostWithDateListReadResponse>> responseDto = ResponseDto.<Page<PostDto.PostWithDateListReadResponse>>success()
                .payload(postListWithDateReadResponsePage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
