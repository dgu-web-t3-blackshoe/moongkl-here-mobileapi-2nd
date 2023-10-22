package com.blackshoe.moongklheremobileapi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.blackshoe.moongklheremobileapi.dto.SkinUrlDto;
import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
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

import java.io.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class SkinServiceTest {

    @Autowired
    private SkinService skinService;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        skinService = new SkinServiceImpl(amazonS3Client);
        ReflectionTestUtils.setField(skinService, "BUCKET", "files.mobile-api.moongkl.com");
        ReflectionTestUtils.setField(skinService, "DISTRIBUTION_DOMAIN", "https://d2tpzr2j83s5i8.cloudfront.net");
        ReflectionTestUtils.setField(skinService, "ROOT_DIRECTORY", "test");
        ReflectionTestUtils.setField(skinService, "SKIN_DIRECTORY", "skin");
    }

    public void deleteFile() {
        amazonS3Client.deleteObject("files.mobile-api.moongkl.com", s3Key);
    }

    private String s3Key;

    private final UUID userId = UUID.randomUUID();

    @Test
    public void uploadSkin_returnsSkinUrlDto_isNotNull() {
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
        final SkinUrlDto skinUrlDto = skinService.uploadSkin(userId, testImg);
        String s3Url = skinUrlDto.getS3Url();
        s3Key = s3Url.substring(s3Url.indexOf("test"));

        //then
        assertThat(skinUrlDto.getS3Url()).isNotNull();
        assertThat(skinUrlDto.getCloudfrontUrl()).isNotNull();
        deleteFile();
    }

    @Test
    public void uploadSkin_emptySkin_error() {
        //given

        //when
        final PostException postException = assertThrows(PostException.class, () -> skinService.uploadSkin(userId, null));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.EMPTY_SKIN);
    }

    @Test
    public void uploadSkin_fileType_error() {
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
        final PostException postException = assertThrows(PostException.class, () -> skinService.uploadSkin(userId, testTxt));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_SKIN_TYPE);
    }

    @Test
    public void uploadSkin_fileSize_error() {
        //given
        final MultipartFile testImg;

        final byte[] bytes = new byte[1024 * 1024 * 60];

        testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", bytes);

        //when
        final PostException postException = assertThrows(PostException.class, () -> skinService.uploadSkin(userId, testImg));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.INVALID_SKIN_SIZE);
    }

    @Test
    public void uploadSkin_server_error() {
        //given
        ReflectionTestUtils.setField(skinService, "BUCKET", "a");
        final MultipartFile testImg;
        try {
            testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", new FileInputStream(new File("src/test/resources/test.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        final PostException postException = assertThrows(PostException.class, () -> skinService.uploadSkin(userId, testImg));

        //then
        assertThat(postException.getPostErrorResult()).isEqualTo(PostErrorResult.SKIN_UPLOAD_FAILED);
    }

    @Test
    public void deleteSkin_success_noError() {
        //given
        final MultipartFile testImg;
        try {
            testImg = new MockMultipartFile("test.jpg", "test.jpg", "img/jpg", new FileInputStream(new File("src/test/resources/test.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final SkinUrlDto skinUrlDto = skinService.uploadSkin(userId, testImg);
        String s3Url = skinUrlDto.getS3Url();
        s3Key = s3Url.substring(s3Url.indexOf("test"));

        //when
        skinService.deleteSkin(s3Url);

        //then
        assertThat(amazonS3Client.doesObjectExist("files.mobile-api.moongkl.com", s3Key)).isFalse();
    }
}
