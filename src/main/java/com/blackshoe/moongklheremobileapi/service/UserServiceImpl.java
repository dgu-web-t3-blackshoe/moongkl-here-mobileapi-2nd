package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public void signIn(UserDto.SignInRequestDto signInRequestDto) {
        //이미 존재하는 회원
        Optional<User> resultUser = userRepository.findByEmail(signInRequestDto.getEmail());

        //TODO: 검증 안된 회원
        userRepository.save(User.builder()
                .email(signInRequestDto.getEmail())
                .password(signInRequestDto.getPassword())
                .nickname(signInRequestDto.getNickname())
                .phoneNumber(signInRequestDto.getPhoneNumber())
                .build());

        log.info("회원가입 성공");
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
