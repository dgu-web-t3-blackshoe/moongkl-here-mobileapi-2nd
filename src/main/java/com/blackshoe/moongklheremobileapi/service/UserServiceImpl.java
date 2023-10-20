package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.BackgroundImgUrlDto;
import com.blackshoe.moongklheremobileapi.dto.JwtDto;
import com.blackshoe.moongklheremobileapi.dto.ProfileImgUrlDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserDto.SignInResponseDto signIn(UserDto.SignInRequestDto signInRequestDto) {
        //이미 존재하는 회원
        userRepository.save(User.builder()
                .email(signInRequestDto.getEmail())
                .password(passwordEncoder.encode(signInRequestDto.getPassword()))
                .nickname(signInRequestDto.getNickname())
                .phoneNumber(signInRequestDto.getPhoneNumber())
                .role(Role.valueOf("USER"))
                .build());

        Optional<User> user = userRepository.findByEmail(signInRequestDto.getEmail());

        log.info("회원가입 성공");
        return UserDto.SignInResponseDto.builder()
                .userId(user.get().getId())
                .createdAt(user.get().getCreatedAt())
                .build();

    }

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

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto.UpdatePasswordResponseDto updatePassword(UserDto.UpdatePasswordRequestDto updatePasswordRequestDto) {

        User originalUser = userRepository.findByEmail(updatePasswordRequestDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        User updatedUser = originalUser.builder()
                .password(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()))
                .build();

        userRepository.save(updatedUser);

        return UserDto.UpdatePasswordResponseDto.builder()
                .userId(updatedUser.getId())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
    }

    @Override
    public boolean userExistsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
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
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserDto.UpdateProfileResponseDto updateProfile(UserDto.UpdateProfileDto updateProfileDto) {

        User user = userRepository.findById(updateProfileDto.getUserId())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));


        final ProfileImgUrl profileImgUrl = ProfileImgUrl.convertProfileImgUrlDtoToEntity(updateProfileDto.getProfileImgUrlDto());
        final BackgroundImgUrl backgroundImgUrl = BackgroundImgUrl.convertBackgroundImgUrlDtoToEntity(updateProfileDto.getBackgroundImgUrlDto());

        user = user.toBuilder()
                .nickname(updateProfileDto.getNickname())
                .statusMessage(updateProfileDto.getStatusMessage())
                .profileImgUrl(profileImgUrl)
                .backgroundImgUrl(backgroundImgUrl)
                .build();

        userRepository.save(user);

        return UserDto.UpdateProfileResponseDto.builder()
                .userId(user.getId())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserDto.UserProfileInfoResponseDto getUserProfileInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrlDto profileImgUrlDto = ProfileImgUrlDto.builder()
                .cloudfrontUrl(user.getProfileImgUrl().getCloudfrontUrl())
                .s3Url(user.getProfileImgUrl().getS3Url())
                .build();

        BackgroundImgUrlDto backgroundImgUrlDto = BackgroundImgUrlDto.builder()
                .cloudfrontUrl(user.getBackgroundImgUrl().getCloudfrontUrl())
                .s3Url(user.getBackgroundImgUrl().getS3Url())
                .build();

        int postcount = user.getPost().size();

        return UserDto.UserProfileInfoResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .statusMessage(user.getStatusMessage())
                .profileImgUrlDto(profileImgUrlDto)
                .backgroundImgUrlDto(backgroundImgUrlDto)
                .likeCount(user.getLikeCount())
                .favoriteCount(user.getFavoriteCount())
                .postCount(postcount)
                .build();
    }

    @Override
    public UserDto.UserBasicProfileInfoResponseDto getUserBasicProfileInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrlDto profileImgUrlDto = ProfileImgUrlDto.builder()
                .cloudfrontUrl(user.getProfileImgUrl().getCloudfrontUrl())
                .s3Url(user.getProfileImgUrl().getS3Url())
                .build();

        int postcount = user.getPost().size();

        return UserDto.UserBasicProfileInfoResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrlDto(profileImgUrlDto)
                .postCount(postcount)
                .build();
    }

    @Override
    public UserDto.UserMyProfileInfoResponseDto getUserMyProfileInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        ProfileImgUrlDto profileImgUrlDto = ProfileImgUrlDto.builder()
                .cloudfrontUrl(user.getProfileImgUrl().getCloudfrontUrl())
                .s3Url(user.getProfileImgUrl().getS3Url())
                .build();

        BackgroundImgUrlDto backgroundImgUrlDto = BackgroundImgUrlDto.builder()
                .cloudfrontUrl(user.getBackgroundImgUrl().getCloudfrontUrl())
                .s3Url(user.getBackgroundImgUrl().getS3Url())
                .build();

        return UserDto.UserMyProfileInfoResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .statusMessage(user.getStatusMessage())
                .profileImgUrlDto(profileImgUrlDto)
                .backgroundImgUrlDto(backgroundImgUrlDto)
                .build();
    }
}
