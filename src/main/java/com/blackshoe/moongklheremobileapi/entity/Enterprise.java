package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "enterprises")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Enterprise {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @JoinColumn(name = "logo_img_url_id", foreignKey = @ForeignKey(name = "enterprise_fk_logo_img_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private LogoImgUrl logoImgUrl;

    @Column(name = "manager_email", nullable = false, length = 50)
    private String managerEmail;

    @Builder
    public Enterprise(UUID id, String name, String country, LogoImgUrl logoImgUrl, String managerEmail) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.logoImgUrl = logoImgUrl;
        this.managerEmail = managerEmail;
    }

    public void updateLogoImgUrl(LogoImgUrl logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
        logoImgUrl.setEnterprise(this);
    }
}
