package com.blackshoe.moongklheremobileapi.entity;


import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "logo_img_urls")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class LogoImgUrl {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column
    private String s3Url;

    @Column
    private String cloudfrontUrl;

    @OneToOne(mappedBy = "logoImgUrl", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Enterprise enterprise;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public LogoImgUrl(UUID id, String s3Url, String cloudfrontUrl, Enterprise enterprise) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
        this.enterprise = enterprise;
    }

//    public static LogoImgUrl convertLogoImgUrlDtoToEntity(EnterpriseDto.LogoImgUrl uploadedLogoImgUrl) {
//        return LogoImgUrl.builder()
//                .s3Url(uploadedLogoImgUrl.getS3Url())
//                .cloudfrontUrl(uploadedLogoImgUrl.getCloudfrontUrl())
//                .build();
//    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }
}

