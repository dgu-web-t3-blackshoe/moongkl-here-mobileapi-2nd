package com.blackshoe.moongklheremobileapi.oauth2;

import com.blackshoe.moongklheremobileapi.dto.JwtDto;
import com.blackshoe.moongklheremobileapi.entity.Role;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import com.blackshoe.moongklheremobileapi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service @RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.redirect-uri}")
    private String REDIRECT_URI;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info(authentication.toString());

        CustomOAuth2User userPrincipal = (CustomOAuth2User) authentication.getPrincipal();
        log.info("userPrincipal {}", userPrincipal.getAttributes().toString());

        String userId = userPrincipal.getName();
        String email = userPrincipal.getEmail();

        log.info("userId {}", userId);
        log.info("email {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_FOUND_USER));

        boolean userSignInStatus = user.getPassword() == null ? false : true;

        JwtDto.JwtRequestDto jwtRequestDto = JwtDto.JwtRequestDto
                .builder()
                .email(email)
                .userId(UUID.fromString(userId))
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(jwtRequestDto);

        response.setStatus(HttpServletResponse.SC_OK);

        response.sendRedirect(REDIRECT_URI + "/social-login?userId=" + userId + "&sign-in=" + String.valueOf(userSignInStatus) + "&access-token=" + accessToken + "&email=" + email);
    }
}
