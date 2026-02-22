package com.blackshoe.moongklheremobileapi.sqs;

import com.blackshoe.moongklheremobileapi.dto.MessageDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.SqsErrorResult;
import com.blackshoe.moongklheremobileapi.repository.*;
import com.blackshoe.moongklheremobileapi.service.PostService;
import com.blackshoe.moongklheremobileapi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsReceiver {

    @Autowired
    private ObjectMapper objectMapper;

    private final EnterpriseRepository enterpriseRepository;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final StoryUrlRepository storyUrlRepository;
    private final NotificationRepository notificationRepository;
    private final PostService postService;
    private final UserService userService;
    private final LogoImgUrlRepository logoImgUrlRepository;
    private final PostRepository postRepository;
    private final TermsRepository termsRepository;
    private final AboutUsRepository aboutUsRepository;

    private static final UUID TERMS_ID = UUID.fromString("4000c0f7-0c97-4bd7-a200-0de1392f1df0");
    private static final UUID ABOUT_US_ID = UUID.fromString("648c4bf4-3c90-492a-bb23-600dae7a4d70");

    @Transactional
    //@SqsListener(value = "MhAdminSaying", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    @SqsListener(value = "${cloud.aws.sqs.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveMessage(final String message) throws IOException {
        //MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);

        MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);

        if (!messageDto.getFrom().equals("mh-admin-api")) {
            log.error("Invalid sender: " + messageDto.getFrom());
            //SqsErrorResult sqsErrorResult = SqsErrorResult.INVALID_SENDER;

            return;
        }

        switch (messageDto.getTopic()) {
            case "create enterprise":
                createEnterprise(messageDto);
                break;
            case "update enterprise":
                updateEnterprise(messageDto);
                break;
            case "delete enterprise":
                deleteEnterprise(messageDto);
                break;
            case "delete enterprise story":
                deleteEnterpriseStory(messageDto);
                break;
            case "create enterprise story":
                createEnterpriseStory(messageDto);
                break;
            case "create notification":
                createNotification(messageDto);
                break;
            case "update notification":
                updateNotification(messageDto);
                break;
            case "pause user":
                pauseUser(messageDto);
                break;
            case "unpause user":
                unpauseUser(messageDto);
                break;
            case "delete user":
                deleteUser(messageDto);
                break;
            case "delete user post":
                deleteUserPost(messageDto);
                break;
            case "update enterprise story visible":
                updateStoryVisible(messageDto);
                break;
            case "update terms":
                updateTerms(messageDto);
                break;
            case "update about us":
                updateAboutUs(messageDto);
                break;
            default:
                log.info("invalid topic : " + messageDto.getTopic());
        }
    }

    private void updateEnterprise(MessageDto messageDto) {
        log.info("update enterprise");

        if(!enterpriseRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("enterprise not exists");
            return;
        }

        Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(messageDto.getMessage().get("id"))).orElseThrow(() -> new RuntimeException("Invalid enterprise id"));

        enterprise.updateEnterprise(messageDto.getMessage().get("country"), messageDto.getMessage().get("managerEmail"));

        LogoImgUrl logoImgUrl = LogoImgUrl.builder()
                .id(UUID.fromString(messageDto.getMessage().get("logoImgUrlId")))
                .s3Url(messageDto.getMessage().get("logoImgUrlS3Url"))
                .cloudfrontUrl(messageDto.getMessage().get("logoImgUrlCloudfrontUrl"))
                .build();

        enterprise.updateLogoImgUrl(logoImgUrl);
        enterpriseRepository.save(enterprise);
    }

    private void deleteEnterpriseStory(MessageDto messageDto){
        log.info("delete enterprise story");

        UUID storyId = UUID.fromString(messageDto.getMessage().get("id"));

        if(!storyUrlRepository.existsById(storyId)){
            log.info("story not exists");
            return;
        }

        storyUrlRepository.deleteById(storyId);
    }

    private void deleteEnterprise(MessageDto messageDto) {
        log.info("delete enterprise");

        if(!enterpriseRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("enterprise not exists");
            return;
        }

        Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(messageDto.getMessage().get("id"))).orElseThrow(() -> new RuntimeException("Invalid enterprise id"));
        logoImgUrlRepository.deleteById(enterprise.getLogoImgUrl().getId());
        enterpriseRepository.deleteById(enterprise.getId());
    }

    @Transactional
    public void deleteUser(MessageDto messageDto) {
        log.info("delete user");

        if(!userRepository.existsById(UUID.fromString(messageDto.getMessage().get("userId")))){
            log.info("user not exists");
            return;
        }

        userService.deleteUserAndRelationships(UUID.fromString(messageDto.getMessage().get("userId")));
    }
    @Transactional
    public void updateStoryVisible(MessageDto messageDto) {
        log.info("update enterprise story visible");

        if(!storyUrlRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("story not exists");
            return;
        }

        StoryUrl storyUrl = storyUrlRepository.findById(UUID.fromString(messageDto.getMessage().get("id"))).orElseThrow(() -> new RuntimeException("Invalid story id"));
        storyUrl.changeIsPublic();

        storyUrlRepository.save(storyUrl);
    }

    @Transactional
    public void deleteUserPost(MessageDto messageDto) {
        log.info("delete user post");

        if(!userRepository.existsById(UUID.fromString(messageDto.getMessage().get("userId")))){
            log.info("user not exists");
            return;
        }

        if(!postRepository.existsById(UUID.fromString(messageDto.getMessage().get("postId")))){
            log.info("post not exists");
            return;
        }

        postService.deletePostRelationships(UUID.fromString(messageDto.getMessage().get("userId")), UUID.fromString(messageDto.getMessage().get("postId")));
    }

    public void pauseUser(MessageDto messageDto){
        log.info("pause user");

        if(!userRepository.existsById(UUID.fromString(messageDto.getMessage().get("userId")))){
            log.info("user not exists");
            return;
        }

        String key = "pause:user:" + messageDto.getMessage().get("userId");
        String value = String.valueOf(messageDto.getMessage().get("pauseDay"));

        Integer pauseDay = Integer.parseInt(value);

        if(pauseDay > 0){
            long timeout = Duration.ofDays(pauseDay).getSeconds();
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeout));
        } else if (pauseDay == 0) {
            // 무제한 정지
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public void unpauseUser(MessageDto messageDto){
        log.info("unpause user");

        if(!userRepository.existsById(UUID.fromString(messageDto.getMessage().get("userId")))){
            log.info("user not exists");
            return;
        }

        String key = "pause:user:" + messageDto.getMessage().get("userId");
        redisTemplate.delete(key);
    }

    @Transactional
    public void updateNotification(MessageDto messageDto) {

        log.info("update notification");

        if(!notificationRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("notification not exists");
            return;
        }

        Notification notification = notificationRepository.findById(UUID.fromString(messageDto.getMessage().get("id"))).orElseThrow(() -> new RuntimeException("Invalid notification id"));

        notification.updateNotification(messageDto.getMessage().get("title"), messageDto.getMessage().get("content"));

        notificationRepository.save(notification);
    }

    @Transactional
    public void createNotification(MessageDto messageDto) {

        log.info("create notification");

        if(notificationRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("notification already exists");
            return;
        }

        Notification newNotification = Notification.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .title(messageDto.getMessage().get("title"))
                .content(messageDto.getMessage().get("content"))
                .build();

        notificationRepository.save(newNotification);
    }

    @Transactional
    public void createEnterprise(MessageDto messageDto) {
        log.info("create enterprise");

        if(enterpriseRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("enterprise already exists");
            return;
        }

        Enterprise enterprise = Enterprise.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .name(messageDto.getMessage().get("name"))
                .country(messageDto.getMessage().get("country"))
                .managerEmail(messageDto.getMessage().get("managerEmail"))
                .build();

        LogoImgUrl logoImgUrl = LogoImgUrl.builder()
                .id(UUID.fromString(messageDto.getMessage().get("logoImgUrlId")))
                .s3Url(messageDto.getMessage().get("logoImgUrlS3Url"))
                .cloudfrontUrl(messageDto.getMessage().get("logoImgUrlCloudfrontUrl"))
                .build();

        enterprise.updateLogoImgUrl(logoImgUrl);
        enterpriseRepository.save(enterprise);
    }

    @Transactional
    public void updateTerms(MessageDto messageDto) {
        log.info("update terms");

        String termsContent = messageDto.getMessage().get("terms");
        if (termsContent == null) {
            log.error("terms content is null");
            return;
        }

        Terms terms = termsRepository.findById(TERMS_ID)
                .orElse(Terms.builder().id(TERMS_ID).terms("").build());

        terms.updateTerms(termsContent);
        termsRepository.save(terms);
    }

    @Transactional
    public void updateAboutUs(MessageDto messageDto) {
        log.info("update about us");

        String aboutUsContent = messageDto.getMessage().get("aboutUs");
        if (aboutUsContent == null) {
            log.error("aboutUs content is null");
            return;
        }

        AboutUs aboutUs = aboutUsRepository.findById(ABOUT_US_ID)
                .orElse(AboutUs.builder().id(ABOUT_US_ID).aboutUs("").build());

        aboutUs.updateAboutUs(aboutUsContent);
        aboutUsRepository.save(aboutUs);
    }

    @Transactional
    public void createEnterpriseStory(MessageDto messageDto) {
        log.info("create enterprise story");

        //story id ,enterprise id
        log.info("story Id : ", messageDto.getMessage().get("id"), "enterprise Id : ", messageDto.getMessage().get("enterpriseId"));

        if(storyUrlRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("story already exists");
            return;
        }

        if(!enterpriseRepository.existsById(UUID.fromString(messageDto.getMessage().get("enterpriseId")))){
            log.info("enterprise not exists");
            return;
        }

        Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(messageDto.getMessage().get("enterpriseId"))).orElseThrow(() -> new RuntimeException("Invalid enterprise id"));

        // create story
        StoryUrl storyUrl = StoryUrl.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .s3Url(messageDto.getMessage().get("s3Url"))
                .cloudfrontUrl(messageDto.getMessage().get("cloudfrontUrl"))
                .isPublic(Boolean.parseBoolean(messageDto.getMessage().get("isPublic")))
                .build();

        storyUrl.updateEnterprise(enterprise);
        storyUrlRepository.save(storyUrl);
    }

}
