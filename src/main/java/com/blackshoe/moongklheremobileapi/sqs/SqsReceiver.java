package com.blackshoe.moongklheremobileapi.sqs;

import com.blackshoe.moongklheremobileapi.dto.MessageDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.entity.Enterprise;
import com.blackshoe.moongklheremobileapi.entity.Notification;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.SqsErrorResult;
import com.blackshoe.moongklheremobileapi.repository.EnterpriseRepository;
import com.blackshoe.moongklheremobileapi.repository.NotificationRepository;
import com.blackshoe.moongklheremobileapi.repository.StoryUrlRepository;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import com.blackshoe.moongklheremobileapi.service.PostService;
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

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsReceiver {

    private final ObjectMapper objectMapper;
    private final EnterpriseRepository enterpriseRepository;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final StoryUrlRepository storyUrlRepository;
    private final NotificationRepository notificationRepository;
    private final PostService postService;
    @SqsListener(value = "MhAdminSaying", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public ResponseEntity<ResponseDto> receiveMessage(final String message) {
        try {
            MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);

            if (!messageDto.getFrom().equals("mh-admin-api")) {
                log.info("Invalid sender");
                SqsErrorResult sqsErrorResult = SqsErrorResult.INVALID_SENDER;

                ResponseDto responseDto = ResponseDto.builder().error(sqsErrorResult.getMessage()).build();
                return ResponseEntity.status(sqsErrorResult.getHttpStatus()).body(responseDto);
            }

            switch (messageDto.getTopic()) {
                case "create enterprise":
                    createEnterprise(messageDto);
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
                case "delete user post":
                    deleteUserPost(messageDto);
                    break;
            }

            return ResponseEntity.ok(ResponseDto.builder().payload("Success " + messageDto.getTopic()).build());
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteUserPost(MessageDto messageDto) {
        log.info("delete user post");
        User user = userRepository.findById(UUID.fromString(messageDto.getMessage().get("userId"))).orElseThrow(() -> new RuntimeException("Invalid user id"));

        postService.deletePost(user, UUID.fromString(messageDto.getMessage().get("postId")));
    }

    private void pauseUser(MessageDto messageDto){
        log.info("pause user");
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

    private void unpauseUser(MessageDto messageDto){
        log.info("unpause user");
        String key = "pause:user:" + messageDto.getMessage().get("userId");
        redisTemplate.delete(key);
    }

    private void updateNotification(MessageDto messageDto) {
        Notification notification = notificationRepository.findById(UUID.fromString(messageDto.getMessage().get("id"))).orElseThrow(() -> new RuntimeException("Invalid notification id"));

        notification.updateNotification(messageDto.getMessage().get("title"), messageDto.getMessage().get("content"));

        notificationRepository.save(notification);
    }

    private void createNotification(MessageDto messageDto) {

        Notification newNotification = Notification.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .title(messageDto.getMessage().get("title"))
                .content(messageDto.getMessage().get("content"))
                .build();

        notificationRepository.save(newNotification);
    }

    private void createEnterprise(MessageDto messageDto) {
        log.info("create enterprise");

        Enterprise enterprise = Enterprise.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .name(messageDto.getMessage().get("name"))
                .country(messageDto.getMessage().get("country"))
                .managerEmail(messageDto.getMessage().get("managerEmail"))
                .build();

        enterpriseRepository.save(enterprise);
    }

    private void createEnterpriseStory(MessageDto messageDto) {
        log.info("create enterprise story");

        Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(messageDto.getMessage().get("enterpriseId"))).orElseThrow(() -> new RuntimeException("Invalid enterprise id"));

        // create story
        StoryUrl storyUrl = StoryUrl.builder()
                .id(UUID.fromString(messageDto.getMessage().get("storyId")))
                .s3Url(messageDto.getMessage().get("s3Url"))
                .cloudfrontUrl(messageDto.getMessage().get("cloudfrontUrl"))
                .build();

        storyUrl.updateEnterprise(enterprise);

        storyUrlRepository.save(storyUrl);
    }

}
