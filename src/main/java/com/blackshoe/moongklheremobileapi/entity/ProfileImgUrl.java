package com.blackshoe.moongklheremobileapi.entity;

import com.blackshoe.moongklheremobileapi.dto.ProfileImgUrlDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "profile_img_urls")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class ProfileImgUrl {
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
    public ProfileImgUrl(UUID id, String s3Url, String cloudfrontUrl) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
    }

    public static ProfileImgUrl convertProfileImgUrlDtoToEntity(ProfileImgUrlDto uploadedProfileImgUrl) {
        return ProfileImgUrl.builder()
                .s3Url(uploadedProfileImgUrl.getS3Url())
                .cloudfrontUrl(uploadedProfileImgUrl.getCloudfrontUrl())
                .build();
    }
}
