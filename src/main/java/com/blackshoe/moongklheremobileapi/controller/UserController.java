package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.SmsDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.service.SmsService;
import com.blackshoe.moongklheremobileapi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    //TODO:messageDto에서 to만 받고 content는 랜덤 문자열 4자릿수 만들어서 redis에 저장, 검증할 때는 redis로 성공 실패여부 파악하기.
    @PostMapping("/sign-in/phone/validation")
    public ResponseEntity<ResponseDto> verification(@RequestBody String phone) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {

        SmsDto.MessageDto messageDto = new SmsDto.MessageDto();
        messageDto.setTo(phone);
        String content = "[뭉클히어] 인증번호는 [" + smsService.makeVerificationCode() + "] 입니다.";
        messageDto.setContent(content);

        SmsDto.SmsResponseDto smsResponseDto = smsService.sendSms(messageDto);

        return null;
    }
}
