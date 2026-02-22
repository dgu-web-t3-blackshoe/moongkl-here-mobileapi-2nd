package com.blackshoe.moongklheremobileapi.entity;

import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "about_us")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class AboutUs {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "about_us", columnDefinition = "TEXT")
    private String aboutUs;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @Builder
    public AboutUs(UUID id, String aboutUs) {
        this.id = id;
        this.aboutUs = aboutUs;
    }

    public void updateAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }
}
