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
import com.blackshoe.moongklheremobileapi.service.PostService;
import com.blackshoe.moongklheremobileapi.service.SkinService;
import com.blackshoe.moongklheremobileapi.service.StoryService;
import com.blackshoe.moongklheremobileapi.service.TemporaryPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TemporaryPostController.class,
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
public class TemporaryPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemporaryPostService temporaryPostService;

    @MockBean
    private SkinService skinService;

    @MockBean
    private StoryService storyService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailService userDetailService;

    public TemporaryPostControllerTest() throws JsonProcessingException {
    }

    @BeforeEach
    public void setUp() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userDetailService.loadUserByUsername(any(String.class))).thenReturn(new UserPrincipal(user));
    }

    private final Logger log = LoggerFactory.getLogger(TemporaryPostControllerTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    final UUID userId = UUID.randomUUID();
    final User user = User.builder()
            .id(userId)
            .email("test")
            .password("test")
            .nickname("test")
            .phoneNumber("test")
            .role(Role.USER)
            .build();

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

    private final TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequest
            = TemporaryPostDto.TemporaryPostCreateRequest.builder()
            .location(skinLocationDto)
            .time(skinTimeDto)
            .build();

    private final String temporaryPostCreateRequestString = objectMapper.writeValueAsString(temporaryPostCreateRequest);

    private final MockMultipartFile temporaryPostCreateRequestFile =
            new MockMultipartFile("temporary_post_create_request", "temporaryPostCreateRequest",
                    "application/json", temporaryPostCreateRequestString.getBytes());

    private final TemporaryPostDto temporaryPostDto = TemporaryPostDto.builder()
            .temporaryPostId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .skin("test")
            .story("test")
            .location(skinLocationDto)
            .time(skinTimeDto)
            .createdAt(LocalDateTime.now())
            .build();

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void createTemporaryPost_whenSuccess_returns201() throws Exception {
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

        when(temporaryPostService.createTemporaryPost((any(User.class)),
                any(SkinUrlDto.class),
                any(StoryUrlDto.class),
                any(TemporaryPostDto.TemporaryPostCreateRequest.class)))
                .thenReturn(temporaryPostDto);

        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/temporary-posts")
                                .file(skin)
                                .file(story)
                                .file(temporaryPostCreateRequestFile)
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
    public void createTemporaryPost_whenEmptyPart_returns400() throws Exception {
        //given

        //when
        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/temporary-posts")
                                .file(story)
                                .file(temporaryPostCreateRequestFile)
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
    public void createTemporaryPost_whenRequestFieldHasMissingField_returns400() throws Exception {
        //given
        final SkinLocationDto skinLocationDtoWithoutLatitude = SkinLocationDto.builder()
                .longitude(0.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequestWithoutLatitude
                = TemporaryPostDto.TemporaryPostCreateRequest.builder()
                .location(skinLocationDtoWithoutLatitude)
                .time(skinTimeDto)
                .build();

        final String temporaryPostCreateRequestStringWithoutLatitude =
                objectMapper.writeValueAsString(temporaryPostCreateRequestWithoutLatitude);

        final MockMultipartFile temporaryPostCreateRequestFileWithoutLatitude =
                new MockMultipartFile("temporary_post_create_request", "temporaryPostCreateRequest",
                        "application/json", temporaryPostCreateRequestStringWithoutLatitude.getBytes());

        //when
        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/temporary-posts")
                                .file(skin)
                                .file(story)
                                .file(temporaryPostCreateRequestFileWithoutLatitude)
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
        final TemporaryPostDto.TemporaryPostCreateRequest temporaryPostCreateRequestWithoutSkinLocation
                = TemporaryPostDto.TemporaryPostCreateRequest.builder()
                .time(skinTimeDto)
                .build();


        final String temporaryPostCreateRequestStringWithoutSkinLocation =
                objectMapper.writeValueAsString(temporaryPostCreateRequestWithoutSkinLocation);

        final MockMultipartFile temporaryPostCreateRequestFileWithoutSkinLocation =
                new MockMultipartFile("temporary_post_create_request", "temporaryPostCreateRequest",
                        "application/json", temporaryPostCreateRequestStringWithoutSkinLocation.getBytes());

        //when
        final MvcResult result = mockMvc.perform(
                        multipart(HttpMethod.POST, "/temporary-posts")
                                .file(skin)
                                .file(story)
                                .file(temporaryPostCreateRequestFileWithoutSkinLocation)
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
    public void getUserTemporaryPostList_whenSuccess_returns200() throws Exception {
        //given
        final Page mockPage = new PageImpl(new ArrayList<>());
        when(temporaryPostService.getUserTemporaryPostList(any(User.class), any(Integer.class), any(Integer.class)))
                .thenReturn(mockPage);

        //when
        final MvcResult result = mockMvc.perform(
                get("/temporary-posts/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("size", "10")
                        .param("page", "0")
                        .with(csrf())
                        .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isOk())
                .andReturn();

        //then
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }

    @Test
    public void getUserTemporaryPostList_whenInvalidUser_return403() throws Exception {
        //given
        final Page mockPage = new PageImpl(new ArrayList<>());
        when(temporaryPostService.getUserTemporaryPostList(any(User.class), any(Integer.class), any(Integer.class)))
                .thenReturn(mockPage);

        //when
        final MvcResult result = mockMvc.perform(
                        get("/temporary-posts/{userId}", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("size", "10")
                                .param("page", "0")
                                .with(csrf())
                                .with(user(userDetailService.loadUserByUsername("test"))))
                .andExpect(status().isForbidden())
                .andReturn();

        //then
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getContentAsString()).isNotEmpty();
        log.info("response: {}", response.getContentAsString());
    }
}
