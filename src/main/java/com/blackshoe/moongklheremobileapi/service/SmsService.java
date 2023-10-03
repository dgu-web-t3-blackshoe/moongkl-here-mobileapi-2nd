package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.SmsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public interface SmsService {
    SmsDto.SmsResponseDto sendSms(SmsDto.MessageDto messageDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException;
    String makeVerificationCode();
    public boolean verifyCode(String phoneNumber, String verificationCode);

    boolean isNotVerified(String phoneNumber);

    void deleteCode(String phoneNumber);
}
