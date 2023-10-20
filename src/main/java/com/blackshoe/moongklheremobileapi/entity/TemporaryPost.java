package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "temporary_posts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class TemporaryPost {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @JoinColumn(name = "skin_url_id", foreignKey = @ForeignKey(name = "temporary_post_fk_skin_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SkinUrl skinUrl;

    @JoinColumn(name = "story_url_id", foreignKey = @ForeignKey(name = "temporary_post_fk_story_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private StoryUrl storyUrl;

    @JoinColumn(name = "skin_time_id", foreignKey = @ForeignKey(name = "temporary_post_fk_skin_time_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SkinTime skinTime;

    @JoinColumn(name = "skin_location_id", foreignKey = @ForeignKey(name = "temporary_post_fk_skin_location_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SkinLocation skinLocation;

    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "temporary_post_fk_user_id"))
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public TemporaryPost(UUID id,
                SkinUrl skinUrl,
                StoryUrl storyUrl,
                SkinTime skinTime,
                SkinLocation skinLocation,
                User user,
                LocalDateTime createdAt) {
        this.id = id;
        this.skinUrl = skinUrl;
        this.storyUrl = storyUrl;
        this.skinTime = skinTime;
        this.skinLocation = skinLocation;
        this.user = user;
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
        user.addTemporaryPost(this);
    }
}
