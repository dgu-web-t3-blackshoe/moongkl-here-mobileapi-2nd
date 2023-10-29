package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
    UserDto.SignUpResponseDto signUp(UserDto.SignUpRequestDto signInRequestDto);

    boolean userExistsByEmail(String email);

    boolean userExistsByNickname(String nickname);

    UserDto.UpdatePasswordResponseDto updatePassword(UserDto.UpdatePasswordRequestDto updatePasswordRequestDto);

    UserDto.LoginResponseDto login(UserDto.LoginRequestDto loginRequestDto);

    boolean userExistsByEmailAndPassword(String email, String password);

    void deleteUser(UUID userId);

    UserDto.UpdateProfileResponseDto updateProfile(UserDto.UpdateProfileDto updateProfileDto);

    UserDto.UserProfileInfoResponseDto getUserProfileInfo(UUID userId);

    UserDto.UserMyProfileInfoResponseDto getUserMyProfileInfo(UUID userId);

    UserDto.UserBasicProfileInfoResponseDto getUserBasicProfileInfo(UUID userId);

    boolean userExistsByIdAndPassword(UUID userId, String password);

    UserDto.UpdatePasswordResponseDto updatePasswordInMyHere(UUID userId, UserDto.UpdatePasswordInMyHereRequestDto updatePasswordInMyHereRequestDto);

    UserDto.UpdatePhoneNumberResponseDto updatePhoneNumber(UUID userId, String phoneNumber);

    UserDto.GetPhoneNumberResponseDto getPhoneNumber(UUID userId);

    UserDto.GetEmailResponseDto getEmail(UUID userId);

    boolean userHasProvider(String email);

    UserDto.SocialSignUpResponseDto socialSignUp(UserDto.SignUpRequestDto signUpRequestDto);

    boolean userExistsByPhoneNumber(String phoneNumber);

    boolean userHasPasswordByEmail(String email);

}
