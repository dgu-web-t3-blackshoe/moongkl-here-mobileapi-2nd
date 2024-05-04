package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.SmsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

//base64
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService{
    private final StringRedisTemplate redisTemplate;
    private final VerificationService verificationService;

//    @Value("${SMS_ACCESS_KEY}")
//    private String accessKey;
//
//    @Value("${SMS_SECRET_KEY}")
//    private String secretKey;

//    @Value("${SMS_SERVICE_ID}")
//    private String serviceId;
//
//    @Value("${SMS_SENDER_NUMBER}")
//    private String senderPhone;

    @Value("${ALIGO_API_KEY}")
    private String aligoApiKey;

    @Value("${ALIGO_USER_ID}")
    private String aligoUserId;

    @Value("${ALIGO_SENDER_KEY}")
    private String aligoSenderKey;

    private String tpl_code = "TS_6629";

    @Value("${ALIGO_SENDER_PHONE_NUMBER}")
    private String senderPhoneNumber;
/*
    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public SmsDto.SmsResponseDto sendSms(SmsDto.MessageDto messageDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        List<SmsDto.MessageDto> messages = new ArrayList<>();
        messages.add(messageDto);

        SmsDto.SmsRequestDto request = SmsDto.SmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(senderPhone)
                .content(messageDto.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsDto.SmsResponseDto response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsDto.SmsResponseDto.class);

        return response;
    }

 */


    public void sendAlimtalk(SmsDto.MessageDto messageDto) throws RestClientException{

        String receiverPhoneNumber = messageDto.getTo();
        String subject_1 = "[뭉클히어]회원가입 인증번호입니다.";
        String message_1 = messageDto.getContent();

        try{
            HttpResponse<JsonNode> response = Unirest.post("https://kakaoapi.aligo.in/akv10/alimtalk/send/")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("apikey", aligoApiKey)
                    .field("userid", aligoUserId)
                    .field("senderkey", aligoSenderKey)
                    .field("tpl_code", tpl_code)
                    .field("receiver_1", receiverPhoneNumber)
                    .field("sender", senderPhoneNumber)
                    .field("subject_1", subject_1)
                    .field("message_1", message_1)
                    .asJson();

            if(response.isSuccess()) {
                JSONObject responseBody = response.getBody().getObject();
                int code = responseBody.getInt("code");

                if(code == 0){
                    log.info("알림톡 전송 성공: {}", responseBody.toString());
                } else {
                    throw new RestClientException("알림톡 전송 실패: " + responseBody.toString());
                }
            }
        } catch (Exception e){
            log.error("알림톡 전송 실패: {}", e.getMessage());
        }
    }
}
