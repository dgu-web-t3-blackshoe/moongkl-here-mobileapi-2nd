package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StoryServiceTest {

    @Autowired
    private StoryService storyService;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        storyService = new StoryServiceImpl(amazonS3Client);
        ReflectionTestUtils.setField(storyService, "BUCKET", "files.mobile-api.moongkl.com");
        ReflectionTestUtils.setField(storyService, "DISTRIBUTION_DOMAIN", "https://d2tpzr2j83s5i8.cloudfront.net");
        ReflectionTestUtils.setField(storyService, "ROOT_DIRECTORY", "test");
        ReflectionTestUtils.setField(storyService, "STORY_DIRECTORY", "story");
    }

    public void deleteFile() {
        amazonS3Client.deleteObject("files.mobile-api.moongkl.com", s3Key);
    }

    private String s3Key;

    private final UUID userId = UUID.randomUUID();

    @Test
    public void 스토리_S3_업로드() {
        //given
        final MultipartFile testImg;
        try {
            testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", new FileInputStream(new File("src/test/resources/test.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        final StoryUrlDto storyUrlDto = storyService.uploadStory(userId, testImg);
        String s3Url = storyUrlDto.getS3Url();
        s3Key = s3Url.substring(s3Url.indexOf("test"));

        //then
        assertThat(storyUrlDto.getS3Url()).isNotNull();
        assertThat(storyUrlDto.getCloudfrontUrl()).isNotNull();
        deleteFile();
    }

    @Test
    public void 스토리_비었을_때_에러처리() {
        //given

        //when
        final PostException postException = assertThrows(PostException.class, () -> storyService.uploadStory(userId, null));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.EMPTY_STORY);
    }

    @Test
    public void 스토리_파일_타입_에러처리() {
        //given
        final MultipartFile testTxt;
        try {
            testTxt = new MockMultipartFile("test.txt", "test.txt", "text/plain", new FileInputStream(new File("src/test/resources/test.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        final PostException postException = assertThrows(PostException.class, () -> storyService.uploadStory(userId, testTxt));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_STORY_TYPE);
    }

    @Test
    public void 스토리_파일_크기_에러처리() {
        //given
        final MultipartFile testImg;

        final byte[] bytes = new byte[1024 * 1024 * 60];

        testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", bytes);

        //when
        final PostException postException = assertThrows(PostException.class, () -> storyService.uploadStory(userId, testImg));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_STORY_SIZE);
    }

    @Test
    public void 스토리_파일_업로드_에러처리() {
        //given
        ReflectionTestUtils.setField(storyService, "BUCKET", "a");
        final MultipartFile testImg;
        try {
            testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", new FileInputStream(new File("src/test/resources/test.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        final PostException postException = assertThrows(PostException.class, () -> storyService.uploadStory(userId, testImg));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.STORY_UPLOAD_FAILED);
    }
}
