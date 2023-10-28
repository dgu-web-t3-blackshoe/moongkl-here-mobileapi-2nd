package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.BackgroundImgUrlDto;
import com.blackshoe.moongklheremobileapi.dto.JwtDto;
import com.blackshoe.moongklheremobileapi.dto.ProfileImgUrlDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.repository.FavoriteRepository;
import com.blackshoe.moongklheremobileapi.repository.LikeRepository;
import com.blackshoe.moongklheremobileapi.repository.ViewRepository;
import com.blackshoe.moongklheremobileapi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
                .role(Role.valueOf("USER"))
                .statusMessage("")
                .profileImgUrl(profileImgUrl)
                .backgroundImgUrl(backgroundImgUrl)
                .build());

        Optional<User> user = userRepository.findByEmail(signUpRequestDto.getEmail());

        log.info("회원가입 성공");
        return UserDto.SignUpResponseDto.builder()
                .userId(user.get().getId())
                .createdAt(user.get().getCreatedAt())
                .build();

    }

    @Transactional
    public UserDto.LoginResponseDto login(UserDto.LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

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
}