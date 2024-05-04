package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.BackgroundImgUrl;
import com.blackshoe.moongklheremobileapi.entity.ProfileImgUrl;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserBlockException;
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
import org.apache.coyote.Response;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.concurrent.TimeUnit;

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

    private final StringRedisTemplate redisTemplate;

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
            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }
        if (!userService.userExistsByEmailAndPassword(loginRequestDto.getEmail(), loginRequestDto.getPassword())) {
            log.info("비밀번호 불일치");
            UserErrorResult userErrorResult = UserErrorResult.NOT_FOUND_USER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }

        checkAndThrowIfPaused(userService.getUserIdByEmail(loginRequestDto.getEmail()));

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

        try{
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
//@TODO: 전화번호 검증
//            if (!verificationService.isVerified(signUpRequestDto.getPhoneNumber())){
//                log.info("검증되지 않은 전화번호");
//                UserErrorResult userErrorResult = UserErrorResult.UNVERIFIED_PHONE_NUMBER;
//                ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();
//
//                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
//            }

            boolean userHasProvider = userService.userHasProvider(signUpRequestDto.getEmail());

            if (userService.userExistsByEmail(signUpRequestDto.getEmail()) && !userHasProvider) {
                log.info("이미 존재하는 이메일");
                UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_EMAIL;

                ResponseDto responseDto = ResponseDto.builder()
                        .error(userErrorResult.getMessage())
                        .build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
            }

            log.info("UserHasProvider: " + userHasProvider);

            if (!userHasProvider) {
                UserDto.SignUpResponseDto signUpResponseDto = userService.signUp(signUpRequestDto);

                ResponseDto responseDto = ResponseDto.builder()
                        .payload(objectMapper.convertValue(signUpResponseDto, Map.class))
                        .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(responseDto); //201

            } else if (userHasProvider) {
                UserDto.SocialSignUpResponseDto socialSignUpResponseDto = userService.socialSignUp(signUpRequestDto);

                ResponseDto responseDto = ResponseDto.builder()
                        .payload(objectMapper.convertValue(socialSignUpResponseDto, Map.class))
                        .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(responseDto); //201
            }
        }catch(Exception e){
            log.info("회원가입 실패");
            ResponseDto responseDto = ResponseDto.builder()
                    .error("회원가입 실패")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto); //400
        }
        return null;
    }
    @PostMapping("/sign-up/email/validation")
    public ResponseEntity<ResponseDto> checkDuplicatedEmail(@Valid @RequestBody UserDto.CheckDuplicatedEmailRequestDto checkDuplicatedEmailRequestDto) {

        String email = checkDuplicatedEmailRequestDto.getEmail();
        if(!userService.userHasProvider(email)) {

            if (userService.userExistsByEmail(email)) {
                log.info("이메일 중복");
                UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_EMAIL;

                ResponseDto responseDto = ResponseDto.builder()
                        .error(userErrorResult.getMessage())
                        .build();

                return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //409
            }
        }else if(userService.userHasProvider(email) && userService.userHasPasswordByEmail(email)){
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
    public ResponseEntity<ResponseDto> updatePassword(@Valid @RequestBody UserDto.UpdatePasswordRequestDto updatePasswordRequestDto) {
        String email = updatePasswordRequestDto.getEmail();
        if (!verificationService.isVerified(email)) {
            log.info("검증되지 않은 이메일");
            UserErrorResult userErrorResult = UserErrorResult.UNVERIFIED_EMAIL;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }
        UserDto.UpdatePasswordResponseDto updatePasswordResponseDto = userService.updatePassword(updatePasswordRequestDto);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(updatePasswordResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //204
    }

    @PostMapping("/sign-up/phone/validation")
    public ResponseEntity<ResponseDto> validationPhoneNumber(@Valid @RequestBody SmsDto.ValidationRequestDto validationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String phoneNumber = validationRequestDto.getPhoneNumber();

        if (userService.userExistsByPhoneNumber(phoneNumber)) {
            log.info("이미 존재하는 전화번호");
            UserErrorResult userErrorResult = UserErrorResult.DUPLICATED_PHONE_NUMBER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto);
        }

        String verificationCode = verificationService.makeVerificationCode();

        SmsDto.MessageDto messageDto = SmsDto.MessageDto.builder()
                .to(phoneNumber)
                .content("[뭉클히어] 인증번호는 [" + verificationCode + "]입니다.")
                .build();

        log.info(verificationCode);

        //SmsDto.SmsResponseDto smsResponseDto = smsService.sendSms(messageDto);
        smsService.sendAlimtalk(messageDto);

        verificationService.saveVerificationCode(phoneNumber, verificationCode);
        verificationService.saveCompletionCode(phoneNumber, false);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }
    @PostMapping("/sign-up/phone/verification")
    public ResponseEntity<ResponseDto> verificationPhoneNumber(@Valid @RequestBody SmsDto.VerificationRequestDto verificationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phone/verification")
    public ResponseEntity<ResponseDto> verificationPhoneNumberInMyHere(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody SmsDto.VerificationRequestDto verificationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
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
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phone/validation")
    public ResponseEntity<ResponseDto> validationPhoneNumberInMyHere(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody SmsDto.ValidationRequestDto validationRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final User user = userPrincipal.getUser();

        String phoneNumber = validationRequestDto.getPhoneNumber();

        String verificationCode = verificationService.makeVerificationCode();

        SmsDto.MessageDto messageDto = SmsDto.MessageDto.builder()
                .to(phoneNumber)
                .content("[뭉클히어]\n가입 인증번호는 " + verificationCode + " 입니다")
                .build();

        log.info(verificationCode);

        smsService.sendAlimtalk(messageDto);

        verificationService.saveVerificationCode(phoneNumber, verificationCode);
        verificationService.saveCompletionCode(phoneNumber, false);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping //API-102
    public ResponseEntity<ResponseDto> deleteUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }

    @GetMapping("/profile/{userId}/details") //API - 133
    public ResponseEntity<ResponseDto> getUserDetailProfileInfo(@PathVariable UUID userId) throws Exception{

        UserDto.UserProfileInfoResponseDto userProfileInfoResponseDto = userService.getUserProfileInfo(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @GetMapping("/profile/{userId}/general") //API - 120
    public ResponseEntity<ResponseDto> getUserBasicProfileInfo(@PathVariable UUID userId) throws Exception{

        UserDto.UserBasicProfileInfoResponseDto userBasicProfileInfoResponseDto = userService.getUserBasicProfileInfo(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(userBasicProfileInfoResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @PreAuthorize("isAuthenticated()")
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

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) // API - 142
    public ResponseEntity<ResponseDto> updateProfile(@RequestPart(name = "profile_img", required = false) MultipartFile profileImg,
                                                     @RequestPart(name = "background_img", required = false) MultipartFile backgroundImg,
                                                     @RequestPart(name = "update_profile_request")UserDto.UpdateProfileRequestDto updateProfileRequestDto,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        log.info("updateProfileRequestDto : {}", updateProfileRequestDto);
        final User user = userPrincipal.getUser();
        log.info("user : {}", user);
        UUID userId = user.getId();

        ProfileImgUrlDto profileImgUrlDto;
        if(profileImg == null){
            profileImgUrlDto = profileService.getUserPresentProfileImgUrlDto(userId);
        }else{
            profileService.deleteProfileImg(userId);
            profileImgUrlDto = profileService.uploadProfileImg(userId, profileImg);
        }

        BackgroundImgUrlDto backgroundImgUrlDto;
        if(backgroundImg == null){
            backgroundImgUrlDto = backgroundService.getUserPresentBackgroundImgUrlDto(userId);
        }else{
            backgroundService.deleteBackgroundImg(userId);
            backgroundImgUrlDto = backgroundService.uploadBackgroundImg(userId, backgroundImg);
        }


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

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password") //API-92
    public ResponseEntity<ResponseDto> updatePasswordInMyHere(@Valid @RequestBody UserDto.UpdatePasswordInMyHereRequestDto updatePasswordInMyHereRequestDto,
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

    @PreAuthorize("isAuthenticated()")
    //API-32 전화번호 변경 전화번호 검증되었으면 전화번호 변경
    @PutMapping("/phone-number") //API-32
    public ResponseEntity<ResponseDto> updatePhoneNumberInMyHere(@Valid @RequestBody UserDto.UpdatePhoneNumberRequestDto updatePhoneNumberRequestDto,
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

    @GetMapping("/{userId}/phone-number")
    public ResponseEntity<ResponseDto> getPhoneNumberWithUserId(@PathVariable UUID userId) throws Exception {

        UserDto.GetPhoneNumberResponseDto getPhoneNumberResponseDto = userService.getPhoneNumber(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(getPhoneNumberResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @GetMapping("/{userId}/email")
    public ResponseEntity<ResponseDto> getEmailWithUserId(@PathVariable UUID userId) throws Exception {
        UserDto.GetEmailResponseDto getEmailResponseDto = userService.getEmail(userId);

        ResponseDto responseDto = ResponseDto.builder()
                .payload(objectMapper.convertValue(getEmailResponseDto, Map.class))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto); //200
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password/validation")
    public ResponseEntity<ResponseDto> validatePassword(@Valid @RequestBody UserDto.ValidatePasswordRequestDto validatePasswordRequestDto,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        final User user = userPrincipal.getUser();

        UUID userId = user.getId();

        if (!userService.userExistsByIdAndPassword(userId, validatePasswordRequestDto.getPassword())) {
            log.info("비밀번호 불일치");
            UserErrorResult userErrorResult = UserErrorResult.NOT_FOUND_USER;
            ResponseDto responseDto = ResponseDto.builder().error(userErrorResult.getMessage()).build();

            return ResponseEntity.status(userErrorResult.getHttpStatus()).body(responseDto); //400
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //204
    }

    //email validation, verification
    @PostMapping("/email/validation")
    public ResponseEntity<ResponseDto> validationEmail(@Valid @RequestBody MailDto.MailRequestDto mailRequestDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String email = mailRequestDto.getEmail();

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
    @PostMapping("/email/verification")
    public ResponseEntity<ResponseDto> verificationEmail(@RequestBody MailDto.MailVerifyDto mailVerifyDto) {
        String email = mailVerifyDto.getEmail();

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

    public void checkAndThrowIfPaused(UUID userId) {
        String key = "pause:user:" + userId.toString();
        String value = redisTemplate.opsForValue().get(key);

        if (value != null) {
            // 키의 남은 만료 시간(초 단위) 조회
            Long expireSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            String message;
            if (expireSeconds == null) {
                message = "영구정지된 유저입니다.";
            } else {
                // 남은 만료 시간을 일수와 시간으로 계산
                long days = TimeUnit.SECONDS.toDays(expireSeconds);
                long hours = TimeUnit.SECONDS.toHours(expireSeconds) - TimeUnit.DAYS.toHours(days);
                message = String.format("정지 기한이 %d일 %d시간 남았습니다.", days, hours);
            }

            throw new UserBlockException(message);
        }
    }
}
