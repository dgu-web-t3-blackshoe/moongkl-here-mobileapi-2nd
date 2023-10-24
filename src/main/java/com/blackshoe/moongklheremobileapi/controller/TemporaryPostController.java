package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.exception.TemporaryPostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.TemporaryPostException;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.PostService;
import com.blackshoe.moongklheremobileapi.service.SkinService;
import com.blackshoe.moongklheremobileapi.service.StoryService;
import com.blackshoe.moongklheremobileapi.service.TemporaryPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/temporary-posts")
public class TemporaryPostController {

    private final SkinService skinService;
    private final StoryService storyService;
    private final TemporaryPostService temporaryPostService;
    private final ObjectMapper objectMapper;

    public TemporaryPostController(SkinService skinService, StoryService storyService, TemporaryPostService temporaryPostService, ObjectMapper objectMapper) {
        this.skinService = skinService;
        this.storyService = storyService;
        this.temporaryPostService = temporaryPostService;
        this.objectMapper = objectMapper;
    }


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto> createTemporaryPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @RequestPart(name = "skin") MultipartFile skin,
                                                           @RequestPart(name = "story") MultipartFile story,
                                                           @RequestPart(name = "temporary_post_create_request") @Valid
                                                           TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest) {

        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        final SkinUrlDto skinUrlDto = skinService.uploadSkin(userId, skin);

        final StoryUrlDto storyUrlDto = storyService.uploadStory(userId, story);

        final TemporaryPostDto temporaryPostDto = temporaryPostService.createTemporaryPost(user, skinUrlDto, storyUrlDto, temporaryPostCreateRequest);

        final TemporaryPostDto.TemporaryPostCreateResponse temporaryPostCreateResponse = TemporaryPostDto.TemporaryPostCreateResponse.builder()
                .temporaryPostId(temporaryPostDto.getTemporaryPostId().toString())
                .createdAt(temporaryPostDto.getCreatedAt().toString())
                .build();

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(temporaryPostCreateResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto> getUserTemporaryPostList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @PathVariable UUID userId,
                                                                @RequestParam Integer size,
                                                                @RequestParam Integer page) {

        final User user = userPrincipal.getUser();

        if (!user.getId().equals(userId)) {
            throw new TemporaryPostException(TemporaryPostErrorResult.USER_NOT_MATCH);
        }

        final Page<TemporaryPostDto.TemporaryPostListReadResponse> temporaryPostListReadResponsePage
                = temporaryPostService.getUserTemporaryPostList(user, size, page);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(temporaryPostListReadResponsePage, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{userId}/{temporaryPostId}")
    public ResponseEntity<ResponseDto> getTemporaryPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @PathVariable UUID temporaryPostId) {

        final User user = userPrincipal.getUser();

        final TemporaryPostDto.TemporaryPostReadResponse temporaryPostReadResponse = temporaryPostService.getTemporaryPost(temporaryPostId, user);

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(temporaryPostReadResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{userId}/{temporaryPostId}")
    public ResponseEntity<ResponseDto> deleteTemporaryPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable UUID temporaryPostId) {

        final User user = userPrincipal.getUser();

        temporaryPostService.deleteTemporaryPost(temporaryPostId, user);

        final TemporaryPostDto.DeleteResponse deleteResponse = TemporaryPostDto.DeleteResponse.builder()
                .temporaryPostId(temporaryPostId.toString())
                .deletedAt(LocalDateTime.now().toString())
                .build();

        final ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(deleteResponse, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }
}
