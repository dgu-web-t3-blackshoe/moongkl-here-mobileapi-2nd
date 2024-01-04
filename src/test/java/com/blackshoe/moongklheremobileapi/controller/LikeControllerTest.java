package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.config.SecurityConfig;
import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Role;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.oauth2.CustomOAuth2UserService;
import com.blackshoe.moongklheremobileapi.oauth2.OAuth2SuccessHandler;
import com.blackshoe.moongklheremobileapi.security.JwtTokenFilter;
import com.blackshoe.moongklheremobileapi.security.JwtTokenProvider;
import com.blackshoe.moongklheremobileapi.security.UserDetailService;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.LikeService;
import com.blackshoe.moongklheremobileapi.service.ViewService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LikeController.class,
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
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

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
    public void likePost_whenSuccess_returns201() throws Exception {
        //given
        final PostDto.LikePostDto increaseViewCountDto
                = PostDto.LikePostDto.builder()
                .postId(UUID.randomUUID())
                .likeCount(1L)
                .userId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();

        //when
        when(likeService.likePost(any(UUID.class), any(User.class))).thenReturn(increaseViewCountDto);
        final MvcResult mvcResult = mockMvc.perform(
                        post("/likes/{postId}", UUID.randomUUID())
                                .contentType("application/json")
                                .accept("application/json")
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
    public void dislikePost_whenSuccess_returns200() throws Exception {
        //given
        final PostDto.DislikePostDto dislikePostDto
                = PostDto.DislikePostDto.builder()
                .postId(UUID.randomUUID())
                .likeCount(0L)
                .userId(UUID.randomUUID())
                .deletedAt(LocalDateTime.now())
                .build();

        //when
        when(likeService.dislikePost(any(UUID.class), any(User.class))).thenReturn(dislikePostDto);
        final MvcResult mvcResult = mockMvc.perform(
                        delete("/likes/{postId}", UUID.randomUUID())
                                .contentType("application/json")
                                .accept("application/json")
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isOk())
                .andReturn();

        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }

    @Test
    public void getUserLikedPostList_whenSuccess_returns200() throws Exception {
        //given
        final Page mockPage = new PageImpl(new ArrayList());

        //when
        when(likeService.getUserLikedPostList(any(User.class), any(Integer.class), any(Integer.class))).thenReturn(mockPage);
        final MvcResult mvcResult = mockMvc.perform(
                        get("/likes/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isOk())
                .andReturn();

        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }

    @Test
    public void getUserLikedPostList_whenInvalidUser_returns403() throws Exception {
        //given

        //when
        final MvcResult mvcResult = mockMvc.perform(
                        get("/likes/{userId}", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isForbidden())
                .andReturn();

        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }
}
