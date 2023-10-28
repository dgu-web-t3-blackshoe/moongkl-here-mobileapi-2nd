package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.dto.BackgroundImgUrlDto;
import com.blackshoe.moongklheremobileapi.entity.BackgroundImgUrl;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Slf4j
public class BackgroundImgServiceImpl implements BackgroundImgService {
    private final AmazonS3Client amazonS3Client;
    private final UserRepository userRepository;

    @Autowired
    public BackgroundImgServiceImpl(AmazonS3Client amazonS3Client, UserRepository userRepository) {
        this.amazonS3Client = amazonS3Client;
        this.userRepository = userRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;
    @Value("${cloud.aws.cloudfront.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${cloud.aws.s3.root-directory}")
    private String ROOT_DIRECTORY;
    @Value("${cloud.aws.s3.background-directory}")
    private String BACKGROUND_DIRECTORY;

    @Override
    @Transactional
    public BackgroundImgUrlDto uploadBackgroundImg(UUID userId, MultipartFile backgroundImg) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        BackgroundImgUrlDto backgroundImgUrlDto;

        if(backgroundImg == null || backgroundImg.isEmpty()) {

            backgroundImgUrlDto = BackgroundImgUrlDto.builder()
                    .cloudfrontUrl("")
                    .s3Url("")
                    .build();

            return backgroundImgUrlDto;
        }

        String s3FilePath = userId + "/" + BACKGROUND_DIRECTORY;

        String fileExtension = backgroundImg.getOriginalFilename().substring(backgroundImg.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;


//        if (!ContentType.isContentTypeValid(backgroundImg.getContentType())) {
//            throw new UserException(UserErrorResult.INVALID_BACKGROUNDIMG_TYPE);
//        }

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

        backgroundImgUrlDto = BackgroundImgUrlDto.builder()
                .cloudfrontUrl(cloudFrontUrl)
                .s3Url(s3Url)
                .build();

        return backgroundImgUrlDto;
    }

    @Override
    @Transactional
    public void deleteBackgroundImg(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        String backgroundImgS3Url = user.getBackgroundImgUrl().getS3Url();

        if(backgroundImgS3Url.equals("")) {
            return;
        }

        String key = backgroundImgS3Url.substring(backgroundImgS3Url.indexOf(ROOT_DIRECTORY));

        try {
            amazonS3Client.deleteObject(BUCKET, key);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(UserErrorResult.BACKGROUNDIMG_DELETE_FAILED);
        }
    }
}
