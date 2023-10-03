package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto.SignInResponseDto signIn(UserDto.SignInRequestDto signInRequestDto) {
        //이미 존재하는 회원
        userRepository.save(User.builder()
                .email(signInRequestDto.getEmail())
                .password(passwordEncoder.encode(signInRequestDto.getPassword()))
                .nickname(signInRequestDto.getNickname())
                .phoneNumber(signInRequestDto.getPhoneNumber())
                .build());

        Optional<User> user = userRepository.findByEmail(signInRequestDto.getEmail());
        log.info("회원가입 성공");
        return UserDto.SignInResponseDto.builder()
                .userId(user.get().getId().toString())
                .createdAt(user.get().getCreatedAt())
                .build();

    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto.UpdatePasswordResponseDto updatePassword(UserDto.UpdatePasswordRequestDto updatePasswordRequestDto, String userId) {

        UUID uuid = UUID.fromString(userId);

        User originalUser = userRepository.findById(uuid)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));


        User updatedUser = originalUser.toBuilder()
                .password(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()))
                .build();

        userRepository.save(updatedUser);

        return UserDto.UpdatePasswordResponseDto.builder()
                .userId(updatedUser.getId().toString())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
    }
}
