package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    UserDto.SignInResponseDto signIn(UserDto.SignInRequestDto signInRequestDto);
    boolean userExistsByEmail(String email);
    UserDto.UpdatePasswordResponseDto updatePassword(UserDto.UpdatePasswordRequestDto updatePasswordRequestDto);
    UserDto.LoginResponseDto login(UserDto.LoginRequestDto loginRequestDto);
}
