package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    public void signIn(UserDto.SignInRequestDto signInRequestDto);
}
