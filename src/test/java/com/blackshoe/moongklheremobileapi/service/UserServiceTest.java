package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.UserDto;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userServiceImpl;
    public User user(){
        return User.builder()
                .email("test@test.com")
                .password("test")
                .nickname("test")
                .phoneNumber("010-1234-5678")
                .build();
    }

    public UserDto.SignInRequestDto signInRequestDto(){
        UserDto.SignInRequestDto signInRequestDto = new UserDto.SignInRequestDto();

        signInRequestDto.setEmail(user().getEmail());
        signInRequestDto.setPassword(user().getPassword());
        signInRequestDto.setNickname(user().getNickname());
        signInRequestDto.setPhoneNumber(user().getPhoneNumber());

        return signInRequestDto;
    }

    @Test
    public void 회원가입실패_필수값누락(){
        //given
        UserDto.SignInRequestDto signInRequestDto = new UserDto.SignInRequestDto();

        signInRequestDto.setEmail(user().getEmail());
        signInRequestDto.setPassword(user().getPassword());
        //필수값 누락
        //signInRequestDto.setNickname(user().getNickname());
        signInRequestDto.setPhoneNumber(user().getPhoneNumber());

        //when
        final UserException result = assertThrows(UserException.class, () -> userServiceImpl.signIn(signInRequestDto));

        //then
        assertThat(result.getUserErrorResult()).isEqualTo(UserErrorResult.REQUIRED_VALUE);
    }

    @Test
    public void 회원가입실패_이미존재함(){
        //given
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail(signInRequestDto().getEmail());

        //when
        final UserException result = assertThrows(UserException.class, () -> userServiceImpl.signIn(signInRequestDto()));

        //then
        assertThat(result.getUserErrorResult()).isEqualTo(UserErrorResult.DUPLICATED_EMAIL);
    }
}
