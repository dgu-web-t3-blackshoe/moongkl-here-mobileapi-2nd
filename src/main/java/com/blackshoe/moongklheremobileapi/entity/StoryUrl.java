package com.blackshoe.moongklheremobileapi.entity;

import com.blackshoe.moongklheremobileapi.dto.StoryUrlDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import reactor.util.annotation.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "story_urls")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class StoryUrl {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String s3Url;

    @Column(nullable = false)
    private String cloudfrontUrl;

    @JoinColumn(name = "enterprise_id", foreignKey = @ForeignKey(name = "story_urls_fk_enterprise_id"), nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Enterprise enterprise;

    @Column(name = "is_public", columnDefinition = "TINYINT(1)")
    private Boolean isPublic;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public StoryUrl(UUID id, String s3Url, String cloudfrontUrl, Boolean isPublic, Enterprise enterprise) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
        this.isPublic = isPublic;
        this.enterprise = enterprise;
        this.createdAt = LocalDateTime.now();
    }

    public static StoryUrl convertStoryUrlDtoToEntity(StoryUrlDto uploadedStoryUrl) {
        final StoryUrl storyUrl = StoryUrl.builder()
                .id(UUID.randomUUID())
                .s3Url(uploadedStoryUrl.getS3Url())
                .cloudfrontUrl(uploadedStoryUrl.getCloudfrontUrl())
                .build();

        return storyUrl;
    }

    public void updateEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public void changeIsPublic(){
        this.isPublic = !this.isPublic;
    }
    public void updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
