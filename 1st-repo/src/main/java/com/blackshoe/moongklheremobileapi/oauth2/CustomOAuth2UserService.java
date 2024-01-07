package com.blackshoe.moongklheremobileapi.oauth2;

import com.blackshoe.moongklheremobileapi.entity.Role;
import com.blackshoe.moongklheremobileapi.exception.UserErrorResult;
import com.blackshoe.moongklheremobileapi.exception.UserException;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import com.blackshoe.moongklheremobileapi.entity.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service @Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    public CustomOAuth2UserService() {
        super();
    }

    @Override
    @Transactional( noRollbackFor = UserException.class )
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            log.info("OAuth2User attributes {} ", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (JsonProcessingException e) {
            log.error("Error while parsing OAuth2User attributes: {}", e.getMessage());
        }
        final String authProvider = userRequest.getClientRegistration().getClientName();
        final String email;

        switch (authProvider.toLowerCase()) {
            case "google":
                log.info("Auth provider: {}", authProvider);
                email = (String) oAuth2User.getAttributes().get("email");
                break;
            case "facebook":
                log.info("Auth provider: {}", authProvider);
                email = (String) oAuth2User.getAttributes().get("email");
                break;
            case "kakao":
                log.info("Auth provider: {}", authProvider);
                Map<String, String> kakaoAccount = (HashMap<String, String>) oAuth2User.getAttributes().get("kakao_account");
                email = (String) kakaoAccount.get("email");
                break;
            case "naver":
                log.info("Auth provider: {}", authProvider);
                Map<String, String> response = (HashMap<String, String>) oAuth2User.getAttributes().get("response");
                email = (String) response.get("email");
                break;
            default:
                throw new AccessDeniedException("Unsupported auth provider: " + authProvider);
        }

        User user = null;
        log.info("Trying to pull user info email {} authProvider {} ", email, authProvider);

        try {
            if(userRepository.existsByEmail(email)) {
                user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

                if (user.getPassword() == null) {
                    log.info("User have to sign in: " + email);
                } else {
                    log.info("User password is null");
                }
                user.setProvider(authProvider);
            }else{
                user = User.builder()
                        .email(email)
                        .role(Role.USER)
                        .provider(authProvider)
                        .build();

                userRepository.save(user);
            }

        } catch (UsernameNotFoundException e) {
            log.error("User not found with email: {}", email);
            throw e;
        }

        if(user==null){
            log.info("User is null");
            throw new UsernameNotFoundException("User 정보 초기화 안됨: " + email);
        }

        log.info("user email {}", user.getEmail());
        log.info("user authProvider {}", user.getProvider());

        return new CustomOAuth2User(user.getId().toString(), user.getEmail(), oAuth2User.getAttributes());
    }
}