package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.config.SecurityConfig;
import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Role;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.oauth2.CustomOAuth2UserService;
import com.blackshoe.moongklheremobileapi.oauth2.OAuth2SuccessHandler;
import com.blackshoe.moongklheremobileapi.security.JwtTokenFilter;
import com.blackshoe.moongklheremobileapi.security.JwtTokenProvider;
import com.blackshoe.moongklheremobileapi.security.UserDetailService;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.FavoriteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FavoriteController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        SecurityConfig.class,
                        JwtTokenProvider.class,
                        JwtTokenFilter.class,
                        UserDetailService.class,
                        CustomOAuth2UserService.class,
                        OAuth2SuccessHandler.class,
                        ClientRegistrationRepository.class
                })
        }
)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc
public class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private UserDetailService userDetailService;

    @BeforeEach
    public void setUp() {
        when(userDetailService.loadUserByUsername(any())).thenReturn(UserPrincipal.create(user));
    }

    private final Logger log = LoggerFactory.getLogger(PostControllerTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UUID userId = UUID.randomUUID();

    final User user = User.builder()
            .id(userId)
            .email("test")
            .password("test")
            .nickname("test")
            .phoneNumber("test")
            .role(Role.USER)
            .build();

    @Test
    public void favoritePost_whenSuccess_returns201() throws Exception {
        //given
        final PostDto.FavoritePostDto favoritePostDto = PostDto.FavoritePostDto.builder()
                .postId(UUID.randomUUID())
                .favoriteCount(1L)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();

        //when
        when(favoriteService.favoritePost(any(UUID.class), any(User.class))).thenReturn(favoritePostDto);
        final MvcResult mvcResult = mockMvc.perform(
                        post("/favorites/{postId}", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }

    @Test
    public void deleteFavoritePost_whenSuccess_returns204() throws Exception {
        //given
        final PostDto.FavoritePostDto deleteFavoritePostDto = PostDto.FavoritePostDto.builder()
                .postId(UUID.randomUUID())
                .favoriteCount(1L)
                .userId(userId)
                .deletedAt(LocalDateTime.now())
                .build();

        //when
        when(favoriteService.favoritePost(any(UUID.class), any(User.class))).thenReturn(deleteFavoritePostDto);
        final MvcResult mvcResult = mockMvc.perform(
                        delete("/favorites/{postId}", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }
}
