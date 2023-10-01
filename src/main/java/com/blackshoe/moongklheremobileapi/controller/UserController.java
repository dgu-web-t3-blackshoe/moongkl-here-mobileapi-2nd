package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/users")
@Controller @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
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

    //TODO:action에 따라 분기 나누기, messageDto에서 to만 받고 content는 랜덤 문자열 4자릿수 만들어서 redis에 저장, 검증할 때는 redis로 성공 실패여부 파악하기.
    @PostMapping("/sign-in/phone?action={action}")
    public ResponseEntity<ResponseDto> sendSms(MessageDTO messageDto, Model model) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        SmsResponseDTO response = smsService.sendSms(messageDto);
        model.addAttribute("response", response);
        return "result";
    }

}
