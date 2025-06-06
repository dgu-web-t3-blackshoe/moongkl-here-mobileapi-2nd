package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @JoinColumn(name = "skin_url_id", foreignKey = @ForeignKey(name = "post_fk_skin_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SkinUrl skinUrl;

    @JoinColumn(name = "story_url_id", foreignKey = @ForeignKey(name = "post_fk_story_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private StoryUrl storyUrl;

    @JoinColumn(name = "skin_time_id", foreignKey = @ForeignKey(name = "post_fk_skin_time_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SkinTime skinTime;

    @JoinColumn(name = "skin_location_id", foreignKey = @ForeignKey(name = "post_fk_skin_location_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SkinLocation skinLocation;

    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "post_fk_user_id"))
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ColumnDefault("0")
    @Column(nullable = false)
    private long likeCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private long favoriteCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private long commentCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private long viewCount;

    @ColumnDefault("false")
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public Post(UUID id,
                SkinUrl skinUrl,
                StoryUrl storyUrl,
                SkinTime skinTime,
                SkinLocation skinLocation,
                User user,
                long likeCount,
                long favoriteCount,
                long commentCount,
                long viewCount,
                boolean isPublic,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.skinUrl = skinUrl;
        this.storyUrl = storyUrl;
        this.skinTime = skinTime;
        this.skinLocation = skinLocation;
        this.user = user;
        this.likeCount = likeCount;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void changeIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseFavoriteCount() {
        this.favoriteCount++;
    }

    public void decreaseFavoriteCount() {
        if (this.favoriteCount > 0) {
            this.favoriteCount--;
        }
    }

    public void setUser(User user) {
        this.user = user;
        user.addPost(this);
    }
}
