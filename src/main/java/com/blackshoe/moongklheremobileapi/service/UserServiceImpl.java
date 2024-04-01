package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.repository.*;
import com.blackshoe.moongklheremobileapi.security.JwtTokenProvider;
import com.blackshoe.moongklheremobileapi.sqs.SqsSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FavoriteRepository favoriteRepository;
    private final ViewRepository viewRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProfileImgService profileImgService;
    private final BackgroundImgService backgroundImgService;
    private final SqsSender sqsSender;
    private final NotificationRepository notificationRepository;
    @Transactional
    public UserDto.SignUpResponseDto signUp(UserDto.SignUpRequestDto signUpRequestDto) {

        ProfileImgUrl profileImgUrl = ProfileImgUrl.builder()
                .cloudfrontUrl("")
                .s3Url("")
                .build();

        BackgroundImgUrl backgroundImgUrl = BackgroundImgUrl.builder()
                .cloudfrontUrl("")
                .s3Url("")
                .build();

        //이미 존재하는 회원
        userRepository.save(User.builder()
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .nickname(signUpRequestDto.getNickname())
                .phoneNumber(signUpRequestDto.getPhoneNumber())
                .gender(signUpRequestDto.getGender())
                .country(signUpRequestDto.getCountry())
                .role(Role.valueOf("USER"))
                .statusMessage("")
                .profileImgUrl(profileImgUrl)
                .backgroundImgUrl(backgroundImgUrl)
                .build());

        Optional<User> user = userRepository.findByEmail(signUpRequestDto.getEmail());

        log.info("회원가입 성공");

        Map<String, String> messageMap = new LinkedHashMap<>();

        /*
                User user = User.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .email(messageDto.getMessage().get("email"))
                .password(messageDto.getMessage().get("password"))
                .phoneNumber(messageDto.getMessage().get("phoneNumber"))
                .gender(messageDto.getMessage().get("gender"))
                .country(messageDto.getMessage().get("country"))
                .createdAt(LocalDateTime.parse(messageDto.getMessage().get("createdAt")))
                .build();
         */

        messageMap.put("id", user.get().getId().toString());
        messageMap.put("email", signUpRequestDto.getEmail());
        messageMap.put("password", signUpRequestDto.getPassword());
        messageMap.put("phoneNumber", signUpRequestDto.getPhoneNumber());
        messageMap.put("gender", signUpRequestDto.getGender());
        messageMap.put("country", signUpRequestDto.getCountry());
        messageMap.put("createdAt", user.get().getCreatedAt().toString());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("create user", messageMap);

        sqsSender.sendToSQS(messageDto);

        return UserDto.SignUpResponseDto.builder()
                .userId(user.get().getId())
                .createdAt(user.get().getCreatedAt())
                .build();

    }

    @Transactional
    public UserDto.LoginResponseDto login(UserDto.LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail());

        JwtDto.JwtRequestDto jwtRequestDto = JwtDto.JwtRequestDto.builder()
                .email(loginRequestDto.getEmail())
                .userId(user.getId())
                .build();

        String jwt = jwtTokenProvider.createAccessToken(jwtRequestDto);

        return UserDto.LoginResponseDto.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .accessToken(jwt)
                .build();
    }
    @Transactional
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDto.UpdatePasswordResponseDto updatePassword(UserDto.UpdatePasswordRequestDto updatePasswordRequestDto) {

        User user = userRepository.findByEmail(updatePasswordRequestDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        user = user.toBuilder()
                .password(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()))
                .build();

        userRepository.save(user);

        return UserDto.UpdatePasswordResponseDto.builder()
                .userId(user.getId())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public boolean userExistsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    @Transactional
    public boolean userExistsByEmailAndPassword(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return true; // 인증 성공
            }
        }

        return false; // 인증 실패
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        likeRepository.deleteAllByUser(user);

        favoriteRepository.deleteAllByUser(user);

        viewRepository.deleteAllByUser(user);

        profileImgService.deleteProfileImg(userId);

        backgroundImgService.deleteBackgroundImg(userId);

        userRepository.deleteById(userId);

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("userId", userId.toString());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("withdraw user", messageMap);
    }

    @Override
    @Transactional
    public UserDto.UpdateProfileResponseDto updateProfile(UserDto.UpdateProfileDto updateProfileDto) {

        User user = userRepository.findById(updateProfileDto.getUserId())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrl userProfileImgUrl = user.getProfileImgUrl();
        BackgroundImgUrl userBackgroundImgUrl = user.getBackgroundImgUrl();

        ProfileImgUrl profileImgUrl;
        if(updateProfileDto.getProfileImgUrlDto().getCloudfrontUrl().equals("")) {

            profileImgUrl = ProfileImgUrl.builder()
                    .cloudfrontUrl("")
                    .s3Url("")
                    .build();
        }else{
            profileImgUrl = ProfileImgUrl.convertProfileImgUrlDtoToEntity(updateProfileDto.getProfileImgUrlDto());
        }

        BackgroundImgUrl backgroundImgUrl;
        if(updateProfileDto.getBackgroundImgUrlDto().getCloudfrontUrl().equals("")) {

            backgroundImgUrl = BackgroundImgUrl.builder()
                    .cloudfrontUrl("")
                    .s3Url("")
                    .build();
        }else{
            backgroundImgUrl = BackgroundImgUrl.convertBackgroundImgUrlDtoToEntity(updateProfileDto.getBackgroundImgUrlDto());
        }

        User updatedUser = user.toBuilder()
                .nickname(updateProfileDto.getNickname())
                .statusMessage(updateProfileDto.getStatusMessage())
                .profileImgUrl(profileImgUrl)
                .backgroundImgUrl(backgroundImgUrl)
                .build();

        log.info("updatedUser {}", updatedUser);
        log.info("user {}", user);

        userRepository.save(updatedUser);

        ProfileImgUrlDto updatedProfileImgUrl = ProfileImgUrlDto.builder()
                .cloudfrontUrl(updatedUser.getProfileImgUrl().getCloudfrontUrl())
                .s3Url(updatedUser.getProfileImgUrl().getS3Url())
                .build();

        BackgroundImgUrlDto updatedBackgroundImgUrl = BackgroundImgUrlDto.builder()
                .cloudfrontUrl(updatedUser.getBackgroundImgUrl().getCloudfrontUrl())
                .s3Url(updatedUser.getBackgroundImgUrl().getS3Url())
                .build();

        return UserDto.UpdateProfileResponseDto.builder()
                .userId(user.getId())
                .updatedAt(user.getUpdatedAt())
                .profileImgUrlDto(updatedProfileImgUrl)
                .backgroundImgUrlDto(updatedBackgroundImgUrl)
                .build();
    }

    @Override
    @Transactional
    public UserDto.UserProfileInfoResponseDto getUserProfileInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrl profileImgUrl = user.getProfileImgUrl();

        ProfileImgUrlDto profileImgUrlDto = ProfileImgUrlDto.builder()
                .cloudfrontUrl(profileImgUrl.getCloudfrontUrl())
                .s3Url(profileImgUrl.getS3Url())
                .build();

        BackgroundImgUrl backgroundImgUrl = user.getBackgroundImgUrl();

        BackgroundImgUrlDto backgroundImgUrlDto = BackgroundImgUrlDto.builder()
                .cloudfrontUrl(backgroundImgUrl.getCloudfrontUrl())
                .s3Url(backgroundImgUrl.getS3Url())
                .build();

        int postCount = user.getPosts().size();
        int likeCount = likeRepository.countByUserId(userId);
        int favoriteCount = favoriteRepository.countByUserId(userId);

        return UserDto.UserProfileInfoResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .statusMessage(user.getStatusMessage())
                .profileImgUrlDto(profileImgUrlDto)
                .backgroundImgUrlDto(backgroundImgUrlDto)
                .likeCount(likeCount)
                .favoriteCount(favoriteCount)
                .postCount(postCount)
                .build();
    }

    @Override
    @Transactional
    public UserDto.UserBasicProfileInfoResponseDto getUserBasicProfileInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrl profileImgUrl = user.getProfileImgUrl();

        ProfileImgUrlDto profileImgUrlDto = ProfileImgUrlDto.builder()
                .cloudfrontUrl(profileImgUrl.getCloudfrontUrl())
                .s3Url(profileImgUrl.getS3Url())
                .build();

        int postCount = user.getPosts().size();

        return UserDto.UserBasicProfileInfoResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrlDto(profileImgUrlDto)
                .postCount(postCount)
                .build();
    }

    @Override
    @Transactional
    public UserDto.UserMyProfileInfoResponseDto getUserMyProfileInfo(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrl profileImgUrl = user.getProfileImgUrl();

        ProfileImgUrlDto profileImgUrlDto = ProfileImgUrlDto.builder()
                .cloudfrontUrl(profileImgUrl.getCloudfrontUrl())
                .s3Url(profileImgUrl.getS3Url())
                .build();

        BackgroundImgUrl backgroundImgUrl = user.getBackgroundImgUrl();

        BackgroundImgUrlDto backgroundImgUrlDto = BackgroundImgUrlDto.builder()
                .cloudfrontUrl(backgroundImgUrl.getCloudfrontUrl())
                .s3Url(backgroundImgUrl.getS3Url())
                .build();

        return UserDto.UserMyProfileInfoResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .statusMessage(user.getStatusMessage())
                .profileImgUrlDto(profileImgUrlDto)
                .backgroundImgUrlDto(backgroundImgUrlDto)
                .build();
    }

    @Override
    @Transactional
    public boolean userExistsByIdAndPassword(UUID userId, String password) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return true; // 인증 성공
            }
        }

        return false; // 인증 실패
    }

    @Override
    @Transactional
    public UserDto.UpdatePasswordResponseDto updatePasswordInMyHere(UUID userId, UserDto.UpdatePasswordInMyHereRequestDto updatePasswordInMyHereRequestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        user = user.toBuilder()
                .password(passwordEncoder.encode(updatePasswordInMyHereRequestDto.getNewPassword()))
                .build();

        userRepository.save(user);

        return UserDto.UpdatePasswordResponseDto.builder()
                .userId(user.getId())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserDto.UpdatePhoneNumberResponseDto updatePhoneNumber(UUID userId, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        user = user.toBuilder()
                .phoneNumber(phoneNumber)
                .build();

        userRepository.save(user);

        return UserDto.UpdatePhoneNumberResponseDto.builder()
                .userId(user.getId())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserDto.GetPhoneNumberResponseDto getPhoneNumber(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        return UserDto.GetPhoneNumberResponseDto.builder()
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public UserDto.GetEmailResponseDto getEmail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        return UserDto.GetEmailResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Override
    public boolean userHasProvider(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getProvider() != null) {
                return true; // 인증 성공
            }
        }

        return false; // 인증 실패
    }

    @Override
    public UserDto.SocialSignUpResponseDto socialSignUp(UserDto.SignUpRequestDto socialSignUpRequestDto) {

        User user = userRepository.findByEmail(socialSignUpRequestDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrl profileImgUrl = ProfileImgUrl.builder()
                .cloudfrontUrl("")
                .s3Url("")
                .build();

        BackgroundImgUrl backgroundImgUrl = BackgroundImgUrl.builder()
                .cloudfrontUrl("")
                .s3Url("")
                .build();

        user = user.toBuilder()
                .nickname(socialSignUpRequestDto.getNickname())
                .password(passwordEncoder.encode(socialSignUpRequestDto.getPassword()))
                .role(Role.valueOf("USER"))
                .phoneNumber(socialSignUpRequestDto.getPhoneNumber())
                .gender(socialSignUpRequestDto.getGender())
                .country(socialSignUpRequestDto.getCountry())
                .statusMessage("")
                .profileImgUrl(profileImgUrl)
                .backgroundImgUrl(backgroundImgUrl)
                .build();

        //이미 존재하는 회원
        userRepository.save(user);

        log.info("user: {}", user);
        log.info("소셜 로그인 후 회원가입 성공");
        return UserDto.SocialSignUpResponseDto.builder()
                .userId(user.getId())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public boolean userExistsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean userHasPasswordByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getPassword() != null) {
                return true; // 인증 성공
            }
        }

        return false; // 인증 실패
    }

    @Override
    public Page<NotificationDto.NotificationReadResponse> getNotification(Integer size, Integer page) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<NotificationDto.NotificationReadResponse> notificationReadResponses = notificationRepository.findAllByOrderByCreatedAtDesc(pageable);

        return notificationReadResponses;
    }

    @Override
    public void sendEnquiry(EnquiryDto.SendEnquiryRequest request) {

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("email", request.getEmail());
        messageMap.put("title", request.getTitle());
        messageMap.put("content", request.getContent());
        messageMap.put("createdAt", LocalDateTime.now().toString());

        sqsSender.sendToSQS(sqsSender.createMessageDtoFromRequest("create enquiry", messageMap));
    }
}