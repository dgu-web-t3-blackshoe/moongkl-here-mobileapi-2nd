package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.blackshoe.moongklheremobileapi.dto.ProfileImgUrlDto;
import com.blackshoe.moongklheremobileapi.entity.ProfileImgUrl;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import com.blackshoe.moongklheremobileapi.vo.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.UUID;


@Slf4j
@Service
public class ProfileImgServiceImpl implements ProfileImgService {
    private final AmazonS3Client amazonS3Client;
    private final UserRepository userRepository;
    @Autowired
    public ProfileImgServiceImpl(AmazonS3Client amazonS3Client, UserRepository userRepository) {
        this.amazonS3Client = amazonS3Client;
        this.userRepository = userRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;
    @Value("${cloud.aws.cloudfront.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${cloud.aws.s3.root-directory}")
    private String ROOT_DIRECTORY;
    @Value("${cloud.aws.s3.profile-directory}")
    private String PROFILEIMG_DIRECTORY;

    @Override
    @Transactional
    public ProfileImgUrlDto uploadProfileImg(UUID userId, MultipartFile profileImg) {

        String s3FilePath = userId + "/" + PROFILEIMG_DIRECTORY;

        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrlDto profileImgUrlDto;

        if(profileImg == null || profileImg.isEmpty()) {
            profileImgUrlDto = ProfileImgUrlDto.builder()
                    .cloudfrontUrl("")
                    .s3Url("")
                    .build();

            return profileImgUrlDto;
        }


        String fileExtension = profileImg.getOriginalFilename().substring(profileImg.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;

//        if (!ContentType.isContentTypeValid(profileImg.getContentType())) {
//            throw new UserException(UserErrorResult.INVALID_PROFILEIMG_TYPE);
//        }

        if (profileImg.getSize() > 52428800) {
            throw new UserException(UserErrorResult.INVALID_PROFILEIMG_SIZE);
        }

        try {
            amazonS3Client.putObject(BUCKET, key, profileImg.getInputStream(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(UserErrorResult.PROFILEIMG_UPLOAD_FAILED);
        }

        String s3Url;

        try {
            s3Url = amazonS3Client.getUrl(BUCKET, key).toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(UserErrorResult.PROFILEIMG_UPLOAD_FAILED);
        }

        String cloudFrontUrl = DISTRIBUTION_DOMAIN + "/" + key;

        profileImgUrlDto = ProfileImgUrlDto.builder()
                .s3Url(s3Url)
                .cloudfrontUrl(cloudFrontUrl)
                .build();

        return profileImgUrlDto;
    }

    @Override
    @Transactional
    public void deleteProfileImg(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));
        String profileImgS3Url = user.getProfileImgUrl().getS3Url();

        if(profileImgS3Url.equals("")){
            return;
        }

        String key = profileImgS3Url.substring(profileImgS3Url.indexOf(ROOT_DIRECTORY));

        try {
            amazonS3Client.deleteObject(BUCKET,  key);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(UserErrorResult.PROFILEIMG_DELETE_FAILED);
        }
    }

    @Override
    @Transactional
    public ProfileImgUrlDto getUserPresentProfileImgUrlDto(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrl profileImgUrl = user.getProfileImgUrl();

        ProfileImgUrlDto profileImgUrlDto = ProfileImgUrlDto.builder()
                .cloudfrontUrl(profileImgUrl.getCloudfrontUrl())
                .s3Url(profileImgUrl.getS3Url())
                .build();

        return profileImgUrlDto;
    }

}
