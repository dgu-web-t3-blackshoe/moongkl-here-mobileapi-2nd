package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.config.AwsS3Config;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PostUploadServiceTest {

    @Autowired
    private PostUploadService postUploadService;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        postUploadService = new PostUploadServiceImpl(amazonS3Client);
        ReflectionTestUtils.setField(postUploadService, "BUCKET", "files.mobile-api.moongkl.com");
        ReflectionTestUtils.setField(postUploadService, "DISTRIBUTION_DOMAIN", "https://d2tpzr2j83s5i8.cloudfront.net");
        ReflectionTestUtils.setField(postUploadService, "ROOT_DIRECTORY", "test");
        ReflectionTestUtils.setField(postUploadService, "SKIN_DIRECTORY", "skin");
    }

    public void deleteFile() {
        amazonS3Client.deleteObject("files.mobile-api.moongkl.com", s3Key);
    }

    private String s3Key;

    private final UUID userId = UUID.randomUUID();

    @Test
    public void 스킨_S3_업로드() {
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
        final SkinUrl skinUrl = postUploadService.uploadSkin(userId, testImg);
        String s3Url = skinUrl.getS3Url();
        s3Key = s3Url.substring(s3Url.indexOf("test"));

        //then
        assertThat(skinUrl.getS3Url()).isNotNull();
        assertThat(skinUrl.getCloudfrontUrl()).isNotNull();
        deleteFile();
    }

    @Test
    public void 스킨_비었을_때_에러처리() {
        //given

        //when
        final PostException postException = assertThrows(PostException.class, () -> postUploadService.uploadSkin(userId, null));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.EMPTY_SKIN);
    }

    @Test
    public void 스킨_파일_타입_에러처리() {
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
        final PostException postException = assertThrows(PostException.class, () -> postUploadService.uploadSkin(userId, testTxt));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_SKIN_TYPE);
    }

    @Test
    public void 스킨_파일_크기_에러처리() {
        //given
        final MultipartFile testImg;

        final byte[] bytes = new byte[1024 * 1024 * 60];

        testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", bytes);

        //when
        final PostException postException = assertThrows(PostException.class, () -> postUploadService.uploadSkin(userId, testImg));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_SKIN_SIZE);
    }

    @Test
    public void 스킨_파일_업로드_에러처리() {
        //given
        ReflectionTestUtils.setField(postUploadService, "BUCKET", "a");
        final MultipartFile testImg;
        try {
            testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", new FileInputStream(new File("src/test/resources/test.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        final PostException postException = assertThrows(PostException.class, () -> postUploadService.uploadSkin(userId, testImg));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.SKIN_UPLOAD_FAILED);
    }
}
