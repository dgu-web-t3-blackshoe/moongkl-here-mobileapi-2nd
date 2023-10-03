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
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SmsService smsService;
    private final ObjectMapper objectMapper;

    private String phoneNumberRegex = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
    private String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private String passwordRegex = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$";

    //닉네임 한글 포함 8자리 이하 특수문자X
    private String nicknameRegex = "^[가-힣a-zA-Z0-9]{1,8}$";

    @PostMapping
    public ResponseEntity<ResponseDto> signIn(@RequestBody UserDto.SignInRequestDto signInRequestDto) throws Exception{
        try {
            if (signInRequestDto.getEmail() == null || signInRequestDto.getPassword() == null || signInRequestDto.getNickname() == null || signInRequestDto.getPhoneNumber() == null) {
                log.info("필수값 누락");
                UserErrorResult userErrorResult = UserErrorResult.REQUIRED_VALUE;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            if (!signInRequestDto.getEmail().matches(emailRegex)) {
                log.info("유효하지 않은 이메일");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            if (!signInRequestDto.getPassword().matches(passwordRegex)) {
                log.info("유효하지 않은 비밀번호");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_PASSWORD;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            if (!signInRequestDto.getNickname().matches(nicknameRegex)) {
                log.info("유효하지 않은 닉네임");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_NICKNAME;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            if (!signInRequestDto.getPhoneNumber().matches(phoneNumberRegex)) {
                log.info("유효하지 않은 전화번호");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_PHONE_NUMBER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if(smsService.isNotVerified(signInRequestDto.getPhoneNumber())) {
                log.info("인증되지 않은 전화번호");
                UserErrorResult userErrorResult = UserErrorResult.UNVERIFIED_PHONE_NUMBER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if (userService.userExistsByEmail(signInRequestDto.getEmail())) {
                log.info("이메일 중복");
                UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_EMAIL;

                ResponseDto responseDto = ResponseDto.builder()
                        .error(userErrorResult.getMessage())
                        .build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            userService.signIn(signInRequestDto);

            return ResponseEntity.status(HttpStatus.CREATED).build(); //201
        }catch (Exception e) {
            log.info("회원가입 실패");
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

    @PostMapping("/sign-in/email/validation")
    public ResponseEntity<ResponseDto> validationEmail(@RequestBody String email) {
        try {
            if(!email.matches(emailRegex)) {
                log.info("유효하지 않은 이메일");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if (userService.userExistsByEmail(email)) {
                log.info("이메일 중복");
                UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_EMAIL;

                ResponseDto responseDto = ResponseDto.builder()
                        .error(userErrorResult.getMessage())
                        .build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
        }catch(Exception e){
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

    //TODO:messageDto에서 to만 받고 content는 랜덤 문자열 4자릿수 만들어서 redis에 저장(Service단에서 저장), 검증할 때는 redis로 성공 실패여부 파악하기. countrycode 받아서 넣기
    @PostMapping("/sign-in/phone/validation")
    public ResponseEntity<ResponseDto> validation(@RequestBody SmsDto.ValidationRequestDto validationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        try {
            String phoneNumber = validationRequestDto.getPhone_number();

            if(!phoneNumber.matches(phoneNumberRegex)) {
                log.info("유효하지 않은 전화번호");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_PHONE_NUMBER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            SmsDto.MessageDto messageDto = SmsDto.MessageDto.builder()
                    .to(phoneNumber)
                    .content("[뭉클히어] 인증번호는 [" + smsService.makeVerificationCode() + "]입니다.")
                    .build();

            SmsDto.SmsResponseDto smsResponseDto = smsService.sendSms(messageDto);
            ResponseDto responseDto = ResponseDto.builder()
                    .payload(objectMapper.convertValue(smsResponseDto, Map.class))
                    .build();

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto); //204
        }catch(Exception e){
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }
    @PostMapping("/sign-in/phone/verification")
    public ResponseEntity<ResponseDto> verification(@RequestBody SmsDto.VerificationRequestDto verificationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        try {
            String phoneNumber = verificationRequestDto.getPhone_number();

            if(!phoneNumber.matches(phoneNumberRegex)) {
                log.info("유효하지 않은 전화번호");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_PHONE_NUMBER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if (smsService.verifyCode(verificationRequestDto.getPhone_number(), verificationRequestDto.getVerification_code())) {
                log.info("인증 코드 검증 성공 성공");
                //검증 성공 후 코드 삭제
                smsService.deleteCode(verificationRequestDto.getPhone_number());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
            } else {
                log.info("인증 코드 검증 실패");
                UserErrorResult userErrorResult = UserErrorResult.FAILED_VALIDATING_CODE;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
        }catch(Exception e){
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }
}
