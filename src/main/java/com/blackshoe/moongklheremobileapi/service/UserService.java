package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
    UserDto.SignInResponseDto signIn(UserDto.SignInRequestDto signInRequestDto);
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
}
