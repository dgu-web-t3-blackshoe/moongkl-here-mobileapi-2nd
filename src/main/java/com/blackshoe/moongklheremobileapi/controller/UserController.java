package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.MailService;
import com.blackshoe.moongklheremobileapi.service.SmsService;
import com.blackshoe.moongklheremobileapi.service.UserService;
import com.blackshoe.moongklheremobileapi.service.VerificationService;
import com.blackshoe.moongklheremobileapi.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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
    @GetMapping("/test")
    public String test() {
        return "test";
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@Valid @RequestBody UserDto.LoginRequestDto loginRequestDto) {
        if (loginRequestDto.getEmail() == null || loginRequestDto.getPassword() == null) {
            log.info("필수값 누락");
            UserErrorResult userErrorResult = UserErrorResult.REQUIRED_VALUE;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }
        if (!userService.userExistsByEmail(loginRequestDto.getEmail())) {
            log.info("존재하지 않는 사용자");
            UserErrorResult userErrorResult = UserErrorResult.NOT_FOUND_USER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();
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
    public ResponseEntity<ResponseDto> signUp(@Valid @RequestBody UserDto.
            SignUpRequestDto signUpRequestDto) throws Exception {
        if (signUpRequestDto.getEmail() == null || signUpRequestDto.getPassword() == null || signUpRequestDto.getNickname() == null || signUpRequestDto.getPhoneNumber() == null) {
            log.info("필수값 누락");
            UserErrorResult userErrorResult = UserErrorResult.REQUIRED_VALUE;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }


        if (userService.userExistsByNickname(signUpRequestDto.getEmail())) {
            log.info("이미 존재하는 닉네임");
            UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_NICKNAME;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }

        if (userService.userExistsByEmail(signUpRequestDto.getEmail())) {
            log.info("이미 존재하는 이메일");
            UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_EMAIL;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }

        if (!verificationService.isVerified(signUpRequestDto.getPhoneNumber())){
            log.info("검증되지 않은 전화번호");
            UserErrorResult userErrorResult = UserErrorResult.UNVERIFIED_PHONE_NUMBER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }


        UserDto.SignUpResponseDto signUpResponseDto = userService.signUp(signUpRequestDto);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(signUpResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto); //201
    }
    @PostMapping("/sign-up/email/validation")
    public ResponseEntity<ResponseDto> checkDuplicatedEmail(@Valid @RequestBody UserDto.CheckDuplicatedEmailRequestDto checkDuplicatedEmailRequestDto) {

        if (userService.userExistsByEmail(checkDuplicatedEmailRequestDto.getEmail())) {
            log.info("이메일 중복");
            UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_EMAIL;

            ResponseDto responseDto = ResponseDto.builder()
                    .error(userErrorResult.getMessage())
                    .build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //409
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }


    @PutMapping("/sign-up/password")
    public ResponseEntity<ResponseDto> updatePassword(@RequestBody UserDto.UpdatePasswordRequestDto updatePasswordRequestDto) {
        String email = updatePasswordRequestDto.getEmail();

        UserDto.UpdatePasswordResponseDto updatePasswordResponseDto = userService.updatePassword(updatePasswordRequestDto);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(updatePasswordResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //204
    }

    @PostMapping("/sign-up/phone/validation")
    public ResponseEntity<ResponseDto> validationPhoneNumber(@RequestBody SmsDto.ValidationRequestDto validationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String phoneNumber = validationRequestDto.getPhoneNumber();

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
    @PostMapping("/sign-up/phone/verification")
    public ResponseEntity<ResponseDto> verificationPhoneNumber(@RequestBody SmsDto.VerificationRequestDto verificationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String phoneNumber = verificationRequestDto.getPhoneNumber();

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

    @PostMapping("/phone/verification")
    public ResponseEntity<ResponseDto> verificationPhoneNumberInMyHere(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody SmsDto.VerificationRequestDto verificationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final User user = userPrincipal.getUser();

        String phoneNumber = verificationRequestDto.getPhoneNumber();

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
    @PostMapping("/phone/validation")
    public ResponseEntity<ResponseDto> validationPhoneNumberInMyHere(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody SmsDto.ValidationRequestDto validationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final User user = userPrincipal.getUser();

        String phoneNumber = validationRequestDto.getPhoneNumber();

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

    @DeleteMapping //API-102
    public ResponseEntity<ResponseDto> deleteUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }

    @GetMapping("/profile/details") //API - 133
    public ResponseEntity<ResponseDto> getUserDetailProfileInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception{
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        UserDto.UserProfileInfoResponseDto userProfileInfoResponseDto = userService.getUserProfileInfo(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }
    @GetMapping("/profile/general") //API - 120
    public ResponseEntity<ResponseDto> getUserBasicProfileInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception{
        final User user = userPrincipal.getUser();
        UserDto.UserBasicProfileInfoResponseDto userBasicProfileInfoResponseDto = userService.getUserBasicProfileInfo(userPrincipal.getUser().getId());

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userBasicProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @GetMapping("/profile") //API - 100
    public ResponseEntity<ResponseDto> getUserMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception{

        final User user = userPrincipal.getUser();
        UUID userId = user.getId();

        UserDto.UserMyProfileInfoResponseDto userMyProfileInfoResponseDto = userService.getUserMyProfileInfo(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userMyProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @PutMapping(value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) // API - 142
    public ResponseEntity<ResponseDto> updateProfile(@RequestPart(name = "profile_img") MultipartFile profileImg,
                                                     @RequestPart(name = "background_img") MultipartFile backgroundImg,
                                                     @RequestPart(name = "update_profile_request")UserDto.UpdateProfileRequestDto updateProfileRequestDto,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        final User user = userPrincipal.getUser();

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
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        if (!userService.userExistsByIdAndPassword(userId, updatePasswordInMyHereRequestDto.getCurrentPassword())) {
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
    public ResponseEntity<ResponseDto> updatePhoneNumberInMyHere(@RequestBody UserDto.UpdatePhoneNumberRequestDto updatePhoneNumberRequestDto,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        String phoneNumber = updatePhoneNumberRequestDto.getPhoneNumber();

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

    @GetMapping("/phone-number")
    public ResponseEntity<ResponseDto> getPhoneNumberInMyHere(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        UserDto.GetPhoneNumberResponseDto getPhoneNumberResponseDto = userService.getPhoneNumber(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(getPhoneNumberResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @GetMapping("/email")
    public ResponseEntity<ResponseDto> getEmailInMyHere(@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        UserDto.GetEmailResponseDto getEmailResponseDto = userService.getEmail(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(getEmailResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }
}
