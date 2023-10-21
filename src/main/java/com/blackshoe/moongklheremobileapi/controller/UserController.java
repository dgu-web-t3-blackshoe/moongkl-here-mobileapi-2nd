package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.MailService;
import com.blackshoe.moongklheremobileapi.service.SmsService;
import com.blackshoe.moongklheremobileapi.service.UserService;
import com.blackshoe.moongklheremobileapi.service.VerificationService;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

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

    private final ProfileImgService profileService;
    private final BackgroundImgService backgroundService;
    private String phoneNumberRegex = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
    private String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private String passwordRegex = "^(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$";

    //닉네임 한글 포함 10자리 이하 특수문자X
    private String nicknameRegex = "^[가-힣a-zA-Z0-9]{1,10}$";

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody UserDto.LoginRequestDto loginRequestDto) {
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
        if (!userService.userExistsByEmail(loginRequestDto.getEmail())) {
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
    }

    @PostMapping
    public ResponseEntity<ResponseDto> signIn(@RequestBody UserDto.SignInRequestDto signInRequestDto) throws Exception {
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

        if (!verificationService.isVerified(signInRequestDto.getPhoneNumber())) {
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
    }

    @PostMapping("/sign-in/email/validation")
    public ResponseEntity<ResponseDto> validationEmail(@RequestBody MailDto.MailRequestDto mailRequestDto) {
        String email = mailRequestDto.getEmail();

        if (!email.matches(emailRegex)) {
            log.info("유효하지 않은 이메일");
            UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }

        if (!userService.userExistsByEmail(email)) {
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
    }

    @PostMapping("/sign-in/email/verification")
    public ResponseEntity<ResponseDto> verificationEmail(@RequestBody MailDto.MailVerifyDto mailVerifyDto) {
        String email = mailVerifyDto.getEmail();

        if (!email.matches(emailRegex)) {
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
    }

    @PutMapping("/sign-in/password")
    public ResponseEntity<ResponseDto> updatePassword(@RequestBody UserDto.UpdatePasswordRequestDto updatePasswordRequestDto) {
        String email = updatePasswordRequestDto.getEmail();
        if (!email.matches(emailRegex)) {
            log.info("유효하지 않은 이메일");
            UserErrorResult userErrorResult = UserErrorResult.INVALID_EMAIL;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }

        if (!verificationService.isVerified(email)) {
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
    }

    @PostMapping("/sign-in/phone/validation")
    public ResponseEntity<ResponseDto> validationPhoneNumber(@RequestBody SmsDto.ValidationRequestDto validationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String phoneNumber = validationRequestDto.getPhoneNumber();

        if (!phoneNumber.matches(phoneNumberRegex)) {
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

        log.info(verificationCode);

        SmsDto.SmsResponseDto smsResponseDto = smsService.sendSms(messageDto);

        verificationService.saveVerificationCode(phoneNumber, verificationCode);
        verificationService.saveCompletionCode(phoneNumber, false);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }

    @PostMapping("/sign-in/phone/verification")
    public ResponseEntity<ResponseDto> verificationPhoneNumber(@RequestBody SmsDto.VerificationRequestDto verificationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String phoneNumber = verificationRequestDto.getPhoneNumber();

        if (!phoneNumber.matches(phoneNumberRegex)) {
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
    }

    @DeleteMapping //API-102
    public ResponseEntity<ResponseDto> deleteUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();

        UUID userId = user.getId();

        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }

    @GetMapping("/profile/details") //API - 133
    public ResponseEntity<ResponseDto> getUserDetailProfileInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception{
        User user = userPrincipal.getUser();

        UUID userId = user.getId();

        UserDto.UserProfileInfoResponseDto userProfileInfoResponseDto = userService.getUserProfileInfo(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }
    @GetMapping("/profile/general") //API - 120
    public ResponseEntity<ResponseDto> getUserBasicProfileInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception{
        User user = userPrincipal.getUser();
        UserDto.UserBasicProfileInfoResponseDto userBasicProfileInfoResponseDto = userService.getUserBasicProfileInfo(userPrincipal.getUser().getId());

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userBasicProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @GetMapping("/profile") //API - 100
    public ResponseEntity<ResponseDto> getUserMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception{

        User user = userPrincipal.getUser();
        UUID userId = user.getId();

        UserDto.UserMyProfileInfoResponseDto userMyProfileInfoResponseDto = userService.getUserMyProfileInfo(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userMyProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }


    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) // API - 142
    public ResponseEntity<ResponseDto> updateProfile(@RequestPart(name = "profile_img") MultipartFile profileImg,
                                                     @RequestPart(name = "background_img") MultipartFile backgroundImg,
                                                     @RequestBody UserDto.UpdateProfileRequestDto updateProfileRequestDto,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        User user = userPrincipal.getUser();

        UUID userId = user.getId();

        final ProfileImgUrlDto profileImgUrlDto = profileService.uploadProfileImg(userId, profileImg);
        final BackgroundImgUrlDto backgroundImgUrlDto = backgroundService.uploadBackgroundImg(userId, backgroundImg);

        UserDto.UpdateProfileDto updateProfileDto = UserDto.UpdateProfileDto.builder()
                .userId(userId)
                .profileImgUrlDto(profileImgUrlDto)
                .backgroundImgUrlDto(backgroundImgUrlDto)
                .nickname(updateProfileRequestDto.getNickname())
                .statusMessage(updateProfileRequestDto.getStatusMessage())
                .build();

        UserDto.UpdateProfileResponseDto updateProfileResponseDto = userService.updateProfile(updateProfileDto);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(updateProfileResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200

    }

    @PutMapping("/password") //API-92
    public ResponseEntity<ResponseDto> updatePasswordInMyHere(@RequestBody UserDto.UpdatePasswordInMyHereRequestDto updatePasswordInMyHereRequestDto,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        User user = userPrincipal.getUser();

        UUID userId = user.getId();

        String currentPassword = updatePasswordInMyHereRequestDto.getCurrentPassword();
        String newPassword = updatePasswordInMyHereRequestDto.getNewPassword();

        if (!currentPassword.matches(passwordRegex)) {
            log.info("currentPassword : 유효하지 않은 비밀번호");
            UserErrorResult userErrorResult = UserErrorResult.INVALID_PASSWORD;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //400
        }

        if (!newPassword.matches(passwordRegex)) {
            log.info("newPassword : 유효하지 않은 비밀번호");
            UserErrorResult userErrorResult = UserErrorResult.INVALID_PASSWORD;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //400
        }

        if (!userService.userExistsByIdAndPassword(userId, currentPassword)) {
            log.info("현재 비밀번호 불일치");
            UserErrorResult userErrorResult = UserErrorResult.NOT_FOUND_USER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //400
        }

        UserDto.UpdatePasswordResponseDto updatePasswordResponseDto = userService.updatePasswordInMyHere(userId, updatePasswordInMyHereRequestDto);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(updatePasswordResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    //API-32 전화번호 변경 전화번호 검증되었으면 전화번호 변경
    @PutMapping("/phone-number") //API-32
    public ResponseEntity<ResponseDto> updatePhoneNumberInMyHere(@RequestBody String phoneNumber,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        User user = userPrincipal.getUser();
        UUID userId = user.getId();

        if (!phoneNumber.matches(phoneNumberRegex)) {
            log.info("유효하지 않은 전화번호");
            UserErrorResult userErrorResult = UserErrorResult.INVALID_PHONE_NUMBER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //400
        }

        if(!verificationService.isVerified(phoneNumber)){
            log.info("검증되지 않은 전화번호");
            UserErrorResult userErrorResult = UserErrorResult.UNVERIFIED_PHONE_NUMBER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //400
        }

        UserDto.UpdatePhoneNumberResponseDto updatePhoneNumberResponseDto = userService.updatePhoneNumber(userId, phoneNumber);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(updatePhoneNumberResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }
}
