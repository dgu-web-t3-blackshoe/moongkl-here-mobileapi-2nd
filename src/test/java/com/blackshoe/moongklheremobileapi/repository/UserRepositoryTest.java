package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    public void signup() {
        // given
        final User user = this.user();
        // when
        final User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(user.getId());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(savedUser.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
    }

    @Test
    public void 회원존재하는지테스트(){
        //given
        final User user = this.user();
        //when
        final Optional<User> savedUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(savedUser).isNull();
    }

    public User user(){
        return
                User.builder()
                .id(1L)
                .userId("test")
                .nickname("test")
                .email("test@test.com")
                .password("test")
                .phoneNumber("010-1234-5678")
                .build();
    }
}
