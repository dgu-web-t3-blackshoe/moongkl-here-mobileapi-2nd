package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.dto.BackgroundImgUrlDto;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.vo.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
public class BackgroundImgServiceImpl implements BackgroundImgService {
    private final AmazonS3Client amazonS3Client;

    public BackgroundImgServiceImpl(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;
    @Value("${cloud.aws.cloudfront.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${cloud.aws.s3.root-directory}")
    private String ROOT_DIRECTORY;
    @Value("${cloud.aws.s3.background-img-directory}")
    private String BACKGROUNDIMG_DIRECTORY;

    @Override
    public BackgroundImgUrlDto uploadBackgroundImg(UUID userId, MultipartFile backgroundImg) {
        if (backgroundImg == null) {
            throw new UserException(UserErrorResult.EMPTY_BACKGROUNDIMG);
        }

        String s3FilePath = userId + "/" + BACKGROUNDIMG_DIRECTORY;

        String fileExtension = backgroundImg.getOriginalFilename().substring(backgroundImg.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;


        if (!ContentType.isContentTypeValid(backgroundImg.getContentType())) {
            throw new UserException(UserErrorResult.INVALID_BACKGROUNDIMG_TYPE);
        }

        if (backgroundImg.getSize() > 52428800) {
            throw new UserException(UserErrorResult.INVALID_BACKGROUNDIMG_SIZE);
        }

        try {
            amazonS3Client.putObject(BUCKET, key, backgroundImg.getInputStream(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(UserErrorResult.BACKGROUNDIMG_UPLOAD_FAILED);
        }

        String s3Url;

        try {
            s3Url = amazonS3Client.getUrl(BUCKET, key).toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(UserErrorResult.BACKGROUNDIMG_UPLOAD_FAILED);
        }

        String cloudFrontUrl = DISTRIBUTION_DOMAIN + "/" + key;

        BackgroundImgUrlDto backgroundImgUrlDto = BackgroundImgUrlDto.builder()
                .s3Url(s3Url)
                .cloudfrontUrl(cloudFrontUrl)
                .build();
        return backgroundImgUrlDto;
    }
}
