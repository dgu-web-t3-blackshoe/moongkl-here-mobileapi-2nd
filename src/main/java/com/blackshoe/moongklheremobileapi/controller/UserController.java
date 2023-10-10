package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.MailDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.SmsDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.oauth2.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.MailService;
import com.blackshoe.moongklheremobileapi.service.SmsService;
import com.blackshoe.moongklheremobileapi.service.UserService;
import com.blackshoe.moongklheremobileapi.service.VerificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SmsService smsService;
    private final VerificationService verificationService;
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private String phoneNumberRegex = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
    private String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private String passwordRegex = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$";

    //닉네임 한글 포함 8자리 이하 특수문자X
    private String nicknameRegex = "^[가-힣a-zA-Z0-9]{1,8}$";
    @GetMapping("/test")
    public ResponseEntity<ResponseDto> test(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        log.info(user.getEmail());
        log.info(user.getNickname());
        log.info(user.getPhoneNumber());
        log.info(user.getRole().toString());
        log.info(user.getCreatedAt().toString());
        log.info(user.getUpdatedAt().toString());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login") //@AuthenticationPrincipal UserPrincipal userPrincipal, User user = userPrincipal.getUser();
    public ResponseEntity<ResponseDto> login(@RequestBody UserDto.LoginRequestDto loginRequestDto) {
        try {
            if (loginRequestDto.getEmail() == null || loginRequestDto.getPassword() == null) {
                log.info("필수값 누락");
                UserErrorResult userErrorResult = UserErrorResult.REQUIRED_VALUE;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            if (!loginRequestDto.getEmail().matches(emailRegex)) {
                log.info("유효하지 않은 이메일");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            if (!userService.userExistsByEmail(loginRequestDto.getEmail())){
                log.info("존재하지 않는 사용자");
                UserErrorResult userErrorResult = UserErrorResult.NOT_FOUND_USER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();
            }
            if (userService.userExistsByNickname(loginRequestDto.getEmail())) {
                log.info("이미 존재하는 닉네임");
                UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_NICKNAME;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            if (!userService.userExistsByEmailAndPassword(loginRequestDto.getEmail(), loginRequestDto.getPassword())) {
                log.info("비밀번호 불일치");
                UserErrorResult userErrorResult = UserErrorResult.NOT_FOUND_USER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            log.info("로그인 성공");
            UserDto.LoginResponseDto loginResponseDto = userService.login(loginRequestDto);

            ResponseDto responseDto = ResponseDto.builder()
                    .payload(objectMapper.convertValue(loginResponseDto, Map.class))
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
        }catch (Exception e) {
            log.info("로그인 실패");
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

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

            if(!verificationService.isVerified(signInRequestDto.getPhoneNumber())) {
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

            UserDto.SignInResponseDto signInResponseDto = userService.signIn(signInRequestDto);

            ResponseDto responseDto = ResponseDto.builder()
                    .payload(objectMapper.convertValue(signInResponseDto, Map.class))
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto); //201
        }catch (Exception e) {
            log.info("회원가입 실패");
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

    @PostMapping("/sign-in/email/validation")
    public ResponseEntity<ResponseDto> validationEmail(@RequestBody MailDto.MailRequestDto mailRequestDto) {
        try {
            String email = mailRequestDto.getEmail();

            if(!email.matches(emailRegex)) {
                log.info("유효하지 않은 이메일");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if(!userService.userExistsByEmail(email)) {
                log.info("존재하지 않는 회원");
                UserErrorResult userErrorResult = UserErrorResult.NOT_FOUND_USER;

                ResponseDto responseDto = ResponseDto.builder()
                        .error(userErrorResult.getMessage())
                        .build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            String verificationCode = verificationService.makeVerificationCode();

            MailDto.MailSendDto mailSendDto = MailDto.MailSendDto.builder()
                    .email(email)
                    .title("뭉클히어 이메일 인증코드입니다.")
                    .content("인증번호는 [" + verificationCode + "]입니다.")
                    .build();
            mailService.sendMail(mailSendDto);

            verificationService.saveVerificationCode(email, verificationCode);
            verificationService.saveCompletionCode(email, false);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
        }catch(Exception e){
            log.info("이메일 인증코드 발송 실패");
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

    @PostMapping("/sign-in/email/verification")
    public ResponseEntity<ResponseDto> verificationEmail(@RequestBody MailDto.MailVerifyDto mailVerifyDto) {
        try {
            String email = mailVerifyDto.getEmail();

            if(!email.matches(emailRegex)) {
                log.info("유효하지 않은 이메일");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if (verificationService.verifyCode(email, mailVerifyDto.getVerificationCode())) {
                log.info("인증 코드 검증 성공");
                //검증 성공 후 코드 삭제, 완료 코드 생성
                verificationService.deleteVerificationCode(email);
                verificationService.saveCompletionCode(email, true);

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

    @PutMapping("/sign-in/password")
    public ResponseEntity<ResponseDto> updatePassword(@RequestBody UserDto.UpdatePasswordRequestDto updatePasswordRequestDto){
        try{
            String email = updatePasswordRequestDto.getEmail();
            if(!email.matches(emailRegex)) {
                log.info("유효하지 않은 이메일");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if(!verificationService.isVerified(email)){
                log.info("검증되지 않은 이메일");
                UserErrorResult userErrorResult = UserErrorResult.UNVERIFIED_EMAIL;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if (!updatePasswordRequestDto.getNewPassword().matches(passwordRegex)) {
                log.info("유효하지 않은 비밀번호");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_PASSWORD;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }
            userService.updatePassword(updatePasswordRequestDto);

            UserDto.UpdatePasswordResponseDto updatePasswordResponseDto = userService.updatePassword(updatePasswordRequestDto);

            ResponseDto responseDto = ResponseDto.builder()
                    .payload(objectMapper.convertValue(updatePasswordResponseDto, Map.class))
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(responseDto); //204

        }catch(Exception e){
            log.info("비밀번호 변경 실패");
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

    @PostMapping("/sign-in/phone/validation")
    public ResponseEntity<ResponseDto> validationPhoneNumber(@RequestBody SmsDto.ValidationRequestDto validationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        try {
            String phoneNumber = validationRequestDto.getPhoneNumber();

            if(!phoneNumber.matches(phoneNumberRegex)) {
                log.info("유효하지 않은 전화번호");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_PHONE_NUMBER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            String verificationCode = verificationService.makeVerificationCode();

            SmsDto.MessageDto messageDto = SmsDto.MessageDto.builder()
                    .to(phoneNumber)
                    .content("[뭉클히어] 인증번호는 [" + verificationCode + "]입니다.")
                    .build();

            SmsDto.SmsResponseDto smsResponseDto = smsService.sendSms(messageDto);

            verificationService.saveVerificationCode(phoneNumber, verificationCode);
            verificationService.saveCompletionCode(phoneNumber, false);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
        }catch(Exception e){
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }
    @PostMapping("/sign-in/phone/verification")
    public ResponseEntity<ResponseDto> verificationPhoneNumber(@RequestBody SmsDto.VerificationRequestDto verificationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        try {
            String phoneNumber = verificationRequestDto.getPhoneNumber();

            if(!phoneNumber.matches(phoneNumberRegex)) {
                log.info("유효하지 않은 전화번호");
                UserErrorResult userErrorResult = UserErrorResult.INVALID_PHONE_NUMBER;
                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            if (verificationService.verifyCode(verificationRequestDto.getPhoneNumber(), verificationRequestDto.getVerificationCode())) {
                log.info("인증 코드 검증 성공");

                verificationService.deleteVerificationCode(verificationRequestDto.getPhoneNumber());
                verificationService.saveCompletionCode(verificationRequestDto.getPhoneNumber(), true);
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
