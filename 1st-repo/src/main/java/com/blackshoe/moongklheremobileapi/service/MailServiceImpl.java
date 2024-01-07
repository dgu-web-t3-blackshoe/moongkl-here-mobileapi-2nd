package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.MailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service @Slf4j @RequiredArgsConstructor
public class MailServiceImpl implements MailService{

    private final JavaMailSender mailSender;
    private final VerificationService verificationService;

    @Value("${spring.mail.username}")
    private String FROM_ADDRESS;

    @Override
    public void sendMail(MailDto.MailSendDto mailSendDto) {

        String email = mailSendDto.getEmail();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom(FROM_ADDRESS);
        mailMessage.setSubject(mailSendDto.getTitle());
        mailMessage.setText(mailSendDto.getContent());

        mailSender.send(mailMessage);

    }

}
