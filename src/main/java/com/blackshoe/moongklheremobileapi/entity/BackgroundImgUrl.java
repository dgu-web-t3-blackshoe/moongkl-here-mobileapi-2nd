package com.blackshoe.moongklheremobileapi.entity;

import com.blackshoe.moongklheremobileapi.dto.BackgroundImgUrlDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "background_img_urls")
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class BackgroundImgUrl{
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String s3Url;

    @Column(nullable = false)
    private String cloudfrontUrl;

    @Builder
    public BackgroundImgUrl(UUID id, String s3Url, String cloudfrontUrl) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
    }

    public static BackgroundImgUrl convertBackgroundImgUrlDtoToEntity(BackgroundImgUrlDto uploadedBackgroundImgUrl) {
        return BackgroundImgUrl.builder()
                .s3Url(uploadedBackgroundImgUrl.getS3Url())
                .cloudfrontUrl(uploadedBackgroundImgUrl.getCloudfrontUrl())
                .build();
    }
}