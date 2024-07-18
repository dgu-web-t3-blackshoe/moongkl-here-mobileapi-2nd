package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.entity.Enterprise;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.EnterpriseRepository;
import com.blackshoe.moongklheremobileapi.repository.StoryUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StoryServiceImpl implements StoryService {

    private final AmazonS3Client amazonS3Client;
    private final StoryUrlRepository storyUrlRepository;
    private final EnterpriseRepository enterpriseRepository;

    public StoryServiceImpl(AmazonS3Client amazonS3Client, StoryUrlRepository storyUrlRepository, EnterpriseRepository enterpriseRepository) {
        this.amazonS3Client = amazonS3Client;
        this.storyUrlRepository = storyUrlRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;
    @Value("${cloud.aws.cloudfront.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${cloud.aws.s3.root-directory}")
    private String ROOT_DIRECTORY;
    @Value("${cloud.aws.s3.story-directory}")
    private String STORY_DIRECTORY;

    @Override
    public StoryUrlDto uploadStory(UUID userId, MultipartFile story) {
        if (story == null) {
            throw new PostException(PostErrorResult.EMPTY_STORY);
        }

        String s3FilePath = userId + "/" + STORY_DIRECTORY;

        String fileExtension = story.getOriginalFilename().substring(story.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;

        //if (!ContentType.isContentTypeValid(story.getContentType())) {
        //    throw new PostException(PostErrorResult.INVALID_STORY_TYPE);
        //}

        if (story.getSize() > 52428800) {
            throw new PostException(PostErrorResult.INVALID_STORY_SIZE);
        }

        try {
            amazonS3Client.putObject(BUCKET, key, story.getInputStream(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PostException(PostErrorResult.STORY_UPLOAD_FAILED);
        }

        String s3Url;

        try {
            s3Url = amazonS3Client.getUrl(BUCKET, key).toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PostException(PostErrorResult.STORY_UPLOAD_FAILED);
        }

        String cloudFrontUrl = DISTRIBUTION_DOMAIN + "/" + key;

        StoryUrlDto storyUrlDto = StoryUrlDto.builder()
                .s3Url(s3Url)
                .cloudfrontUrl(cloudFrontUrl)
                .build();

        return storyUrlDto;
    }

    @Override
    public void deleteStory(String storyS3Url) {
        String key = storyS3Url.substring(storyS3Url.indexOf(ROOT_DIRECTORY));

        try {
            amazonS3Client.deleteObject(BUCKET, key);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PostException(PostErrorResult.STORY_DELETE_FAILED);
        }
    }

    @Override
    public Page<PostDto.EnterpriseStoryReadResponse> getEnterpriseStory(Integer size, Integer page) {

        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");

        final Pageable pageable = PageRequest.of(page, size, sortBy);

        final Page<PostDto.EnterpriseStoryReadResponse> enterpriseStoryReadResponsePage
                = storyUrlRepository.findAllEnterpriseStory(pageable);

        return enterpriseStoryReadResponsePage;
    }

    @Override
    public Page<PostDto.EnterpriseSearchReadResponse> searchEnterprise(String enterpriseName, Integer size, Integer page) {
        List<Enterprise> enterprises = enterpriseRepository.findByNameContainingOrderByStoryCreatedAtDesc(enterpriseName);

        List<PostDto.EnterpriseSearchReadResponse> responseList = enterprises.stream()
                .map(enterprise -> {
                    List<StoryUrl> storyUrlList = storyUrlRepository.findByEnterpriseOrderByCreatedAtDesc(enterprise);
                    List<PostDto.EnterpriseStoryList> enterpriseStoryList = storyUrlList.stream()
                            .map(storyUrl -> PostDto.EnterpriseStoryList.builder()
                                    .storyId(storyUrl.getId())
                                    .cloudfrontUrl(storyUrl.getCloudfrontUrl())
                                    .createdAt(storyUrl.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList());

                    return PostDto.EnterpriseSearchReadResponse.builder()
                            .enterpriseId(enterprise.getId())
                            .enterpriseName(enterprise.getName())
                            .enterpriseStoryList(enterpriseStoryList)
                            .build();
                })
                .collect(Collectors.toList());

        int start = page * size;
        int end = Math.min((start + size), responseList.size());
        List<PostDto.EnterpriseSearchReadResponse> pageContent = responseList.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(page, size), responseList.size());
    }
}
