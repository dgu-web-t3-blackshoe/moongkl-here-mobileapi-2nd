package com.blackshoe.moongklheremobileapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service @Slf4j @RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService{

    private final StringRedisTemplate redisTemplate;

    public String makeVerificationCode() {
        String verificationCode = "";
        for (int i = 0; i < 4; i++) {
            verificationCode += (int)(Math.random() * 10);
        }
        return verificationCode;
    }

    //5분의 인증코드
    public void saveVerificationCode(String key, String verificationCode) {
        redisTemplate.opsForValue().set(key, verificationCode, 5, TimeUnit.MINUTES);
    }

    public boolean verifyCode(String key, String verificationCode) {
        String code = redisTemplate.opsForValue().get(key);
        return code.equals(verificationCode);
    }

    public void deleteCode(String key) {
        redisTemplate.delete(key);
    }

    public boolean isExistsValidationCode(String key) {
        return redisTemplate.hasKey(key);
    }
}
