package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.SmsDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.service.SmsService;
import com.blackshoe.moongklheremobileapi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RequestMapping("/users")
@Controller @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SmsService smsService;

    //모든 userException : 400
    @PostMapping
    public void signIn(@RequestBody UserDto.SignInRequestDto signInRequestDto) throws Exception{
        //필수값 누락
        if (signInRequestDto.getEmail() == null || signInRequestDto.getPassword() == null || signInRequestDto.getNickname() == null || signInRequestDto.getPhoneNumber() == null) {
            log.info("필수값 누락");
            throw new UserException(UserErrorResult.REQUIRED_VALUE);
        }
        try {
            userService.signIn(signInRequestDto);
        } catch (Exception e) {
            log.info("회원가입 실패");
        }
    }

    @PostMapping("/sign-in/email/validation")
    public ResponseEntity<ResponseDto> validationEmail(@RequestBody String email) {
        if(userService.userExistsByEmail(email)){
            log.info("이메일 중복");
            UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_EMAIL;

            ResponseDto responseDto = ResponseDto.builder()
                    .error(userErrorResult.getMessage())
                    .build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }
/*
    //TODO:action에 따라 분기 나누기, messageDto에서 to만 받고 content는 랜덤 문자열 4자릿수 만들어서 redis에 저장, 검증할 때는 redis로 성공 실패여부 파악하기.
    @PostMapping("/sign-in/phone?action={action}")
    public ResponseEntity<ResponseDto> verification(SmsDto.MessageDto messageDto, @RequestParam String action) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        switch (action) {
            case "validation":

                break;
            case "verification":
                break;

            default:
                break;
        }
        SmsDto.SmsResponseDto smsResponseDto = smsService.sendSms(messageDto);

        return "result";
    }
*/
    public String makeVerificationCode() {
        String verificationCode = "";
        for (int i = 0; i < 4; i++) {
            verificationCode += (int)(Math.random() * 10);
        }
        return verificationCode;
    }
}
