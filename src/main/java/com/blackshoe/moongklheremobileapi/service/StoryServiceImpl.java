package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.vo.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class StoryServiceImpl implements StoryService {

    private final AmazonS3Client amazonS3Client;

    public StoryServiceImpl(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
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
    public StoryUrl uploadStory(UUID userId, MultipartFile story) {
        if (story == null) {
            throw new PostException(PostErrorResult.EMPTY_STORY);
        }

        String s3FilePath = userId + "/" + STORY_DIRECTORY;

        String fileExtension = story.getOriginalFilename().substring(story.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;

        if (!ContentType.isContentTypeValid(story.getContentType())) {
            throw new PostException(PostErrorResult.INVALID_STORY_TYPE);
        }

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

        StoryUrl storyUrl = StoryUrl.builder()
                .s3Url(s3Url)
                .cloudfrontUrl(cloudFrontUrl)
                .build();

        return storyUrl;
    }
}
