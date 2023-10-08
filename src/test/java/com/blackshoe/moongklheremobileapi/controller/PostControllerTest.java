package com.blackshoe.moongklheremobileapi.controller;

import com.blackshoe.moongklheremobileapi.config.SecurityConfig;
import com.blackshoe.moongklheremobileapi.dto.*;
import com.blackshoe.moongklheremobileapi.entity.Role;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.oauth2.CustomOAuth2UserService;
import com.blackshoe.moongklheremobileapi.oauth2.OAuth2SuccessHandler;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import com.blackshoe.moongklheremobileapi.security.JwtTokenFilter;
import com.blackshoe.moongklheremobileapi.security.JwtTokenProvider;
import com.blackshoe.moongklheremobileapi.security.UserDetailService;
import com.blackshoe.moongklheremobileapi.security.UserPrincipal;
import com.blackshoe.moongklheremobileapi.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class,
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
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private SkinService skinService;

    @MockBean
    private StoryService storyService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailService userDetailService;

    public PostControllerTest() throws JsonProcessingException {
    }

    @BeforeEach
    public void setUp() {
        final User user = User.builder()
                .id(UUID.randomUUID())
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        when(userDetailService.loadUserByUsername(any())).thenReturn(UserPrincipal.create(user));
    }

    private final Logger log = LoggerFactory.getLogger(PostControllerTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();


    private final MockMultipartFile skin = new MockMultipartFile("skin", "test", "image/png", "test".getBytes());

    private final MockMultipartFile story = new MockMultipartFile("story", "test", "image/png", "test".getBytes());

    private final SkinLocationDto skinLocationDto = SkinLocationDto.builder()
            .latitude(0.0)
            .longitude(0.0)
            .country("test")
            .state("test")
            .city("test")
            .build();

    private final SkinTimeDto skinTimeDto = SkinTimeDto.builder()
            .year(2021)
            .month(1)
            .day(1)
            .hour(0)
            .minute(0)
            .build();

    private final PostDto.PostCreateRequest postCreateRequest = PostDto.PostCreateRequest.builder()
            .location(skinLocationDto)
            .time(skinTimeDto)
            .isPublic(true)
            .build();

    private final String postCreateRequestString = objectMapper.writeValueAsString(postCreateRequest);

    private final MockMultipartFile postCreateRequestFile =
            new MockMultipartFile("post_create_request", "postCreateRequest",
                    "application/json", postCreateRequestString.getBytes());

    private final PostDto postDto = PostDto.builder()
            .postId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .skin("test")
            .story("test")
            .commentCount(0)
            .likeCount(0)
            .viewCount(0)
            .favoriteCount(0)
            .isPublic(true)
            .createdAt(LocalDateTime.now())
            .build();

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void createPost_whenSuccess_returns201() throws Exception {
        //given


        //when
        when(skinService.uploadSkin(any(UUID.class), any(MultipartFile.class)))
                .thenReturn(SkinUrlDto.builder()
                        .s3Url("test")
                        .cloudfrontUrl("test")
                        .build());

        when(storyService.uploadStory(any(UUID.class), any(MultipartFile.class)))
                .thenReturn(StoryUrlDto.builder()
                        .s3Url("test")
                        .cloudfrontUrl("test")
                        .build());

        when(postService.createPost((any(User.class)),
                any(SkinUrlDto.class),
                any(StoryUrlDto.class),
                any(PostDto.PostCreateRequest.class)))
                .thenReturn(postDto);

        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/posts")
                                .file(skin)
                                .file(story)
                                .file(postCreateRequestFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }

    @Test
    public void createPost_whenEmptyPart_returns400() throws Exception {
        //given

        //when
        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/posts")
                                .file(story)
                                .file(postCreateRequestFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }

    @Test
    public void createPost_whenRequestFieldHasMissingField_returns400() throws Exception {
        //given
        final SkinLocationDto skinLocationDtoWithoutLatitude = SkinLocationDto.builder()
                .longitude(0.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final PostDto.PostCreateRequest postCreateRequestWithoutLatitude = PostDto.PostCreateRequest.builder()
                .location(skinLocationDtoWithoutLatitude)
                .time(skinTimeDto)
                .isPublic(true)
                .build();

        final String postCreateRequestStringWithoutLatitude =
                objectMapper.writeValueAsString(postCreateRequestWithoutLatitude);

        final MockMultipartFile postCreateRequestFileWithoutLatitude =
                new MockMultipartFile("post_create_request", "postCreateRequest",
                        "application/json", postCreateRequestStringWithoutLatitude.getBytes());

        //when
        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/posts")
                                .file(skin)
                                .file(story)
                                .file(postCreateRequestFileWithoutLatitude)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }

    @Test
    public void createPost_whenRequestHasMissingField_returns400() throws Exception {
        //given
        final PostDto.PostCreateRequest postCreateRequestWithoutSkinLocation = PostDto.PostCreateRequest.builder()
                .time(skinTimeDto)
                .isPublic(true)
                .build();

        final String postCreateRequestStringWithoutSkinLocation =
                objectMapper.writeValueAsString(postCreateRequestWithoutSkinLocation);

        final MockMultipartFile postCreateRequestFileWithoutSkinLocation =
                new MockMultipartFile("post_create_request", "postCreateRequest",
                        "application/json", postCreateRequestStringWithoutSkinLocation.getBytes());

        //when
        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/posts")
                                .file(skin)
                                .file(story)
                                .file(postCreateRequestFileWithoutSkinLocation)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }
}
