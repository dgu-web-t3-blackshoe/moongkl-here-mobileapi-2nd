package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
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
public class PostUploadServiceImpl implements PostUploadService {

    private final AmazonS3Client amazonS3Client;

    public PostUploadServiceImpl(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;
    @Value("${cloud.aws.cloudfront.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${cloud.aws.s3.root-directory}")
    private String ROOT_DIRECTORY;
    @Value("${cloud.aws.s3.skin-directory}")
    private String SKIN_DIRECTORY;

    @Override
    public SkinUrl uploadSkin(UUID userId, MultipartFile skin) {
        if (skin == null) {
            throw new PostException(PostErrorResult.EMPTY_SKIN);
        }

        String s3FilePath = userId + "/" + SKIN_DIRECTORY;

        String fileExtension = skin.getOriginalFilename().substring(skin.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;

        if (!ContentType.isContentTypeValid(skin.getContentType())) {
            throw new PostException(PostErrorResult.INVALID_SKIN_TYPE);
        }

        if (skin.getSize() > 52428800) {
            throw new PostException(PostErrorResult.INVALID_SKIN_SIZE);
        }

        try {
            amazonS3Client.putObject(BUCKET, key, skin.getInputStream(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PostException(PostErrorResult.SKIN_UPLOAD_FAILED);
        }

        String s3Url;

        try {
            s3Url = amazonS3Client.getUrl(BUCKET, key).toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PostException(PostErrorResult.SKIN_UPLOAD_FAILED);
        }

        String cloudFrontUrl = DISTRIBUTION_DOMAIN + "/" + key;

        SkinUrl skinUrl = SkinUrl.builder()
                .s3Url(s3Url)
                .cloudfrontUrl(cloudFrontUrl)
                .build();

        return skinUrl;
    }
}
