package com.blackshoe.moongklheremobileapi.service;

public interface VerificationService {
    String makeVerificationCode();
    void saveVerificationCode(String key, String verificationCode);
    boolean verifyCode(String key, String verificationCode);
    void deleteVerificationCode(String key);
    void saveCompletionCode(String key, boolean status);
    boolean isVerified(String key);
}
