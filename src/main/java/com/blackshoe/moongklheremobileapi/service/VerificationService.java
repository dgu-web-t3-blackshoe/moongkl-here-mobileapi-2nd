package com.blackshoe.moongklheremobileapi.service;

public interface VerificationService {
    String makeVerificationCode();
    void saveVerificationCode(String key, String verificationCode);
    boolean verifyCode(String key, String verificationCode);

    boolean isExistsValidationCode(String key);

    void deleteCode(String key);
}
