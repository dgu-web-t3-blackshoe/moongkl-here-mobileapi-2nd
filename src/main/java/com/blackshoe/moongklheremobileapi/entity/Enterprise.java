package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "enterprises")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Enterprise {
    @Id
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

    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL)
    private Set<StoryUrl> storyUrls;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public Enterprise(UUID id, String name, String country, LogoImgUrl logoImgUrl, String managerEmail) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.logoImgUrl = logoImgUrl;
        this.managerEmail = managerEmail;
        this.storyUrls = new HashSet<>();
    }
    public void updateLogoImgUrl(LogoImgUrl logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
        logoImgUrl.setEnterprise(this);
    }

    public void updateCountry(String country){
        this.country = country;
    }
    public void updateManagerEmail(String managerEmail){
        this.managerEmail = managerEmail;
    }

    public void updateEnterprise(String country, String managerEmail) {
        this.country = country;
        this.managerEmail = managerEmail;
    }
}
