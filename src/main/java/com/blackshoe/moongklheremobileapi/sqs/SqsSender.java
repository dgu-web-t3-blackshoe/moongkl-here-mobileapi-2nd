package com.blackshoe.moongklheremobileapi.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.blackshoe.moongklheremobileapi.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SqsSender {

    @Value("${cloud.aws.sqs.queue-url}")
    private String queueUrl;

    @Autowired
    private AmazonSQSAsyncClient amazonSQSAsyncClient;

    @Autowired
    private ObjectMapper objectMapper;


//    public void sendToSqsFifo(MessageDto messageDto, String messageGroupID) {
//
//        String jsonMessageBody;
//        try {
//            jsonMessageBody = objectMapper.writeValueAsString(messageDto); // 객체를 JSON 문자열로 변환
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Error converting messageDto to JSON", e);
//        }
//
//        SendMessageRequest sendMessageRequest = new SendMessageRequest()
//                .withQueueUrl(queueUrl)
//                .withMessageBody(jsonMessageBody)
//                .withMessageGroupId(messageGroupID)
//                .withMessageDeduplicationId(UUID.randomUUID().toString());
//
//        amazonSQSAsyncClient.sendMessage(sendMessageRequest);
//
//    }

    public void sendToSQS(MessageDto messageDto) {
        log.info("Sending message");
        String jsonMessageBody;
        try {
            jsonMessageBody = objectMapper.writeValueAsString(messageDto); // 객체를 JSON 문자열로 변환
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting messageDto to JSON", e);
        }

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(jsonMessageBody);

        amazonSQSAsyncClient.sendMessage(sendMessageRequest);
    }

    public MessageDto createMessageDtoFromRequest(String topic, Map<String, String> message) {
        return MessageDto.builder()
                .from("mh-app-api")
                .topic(topic)
                .message(message)
                .build();
    }
}
