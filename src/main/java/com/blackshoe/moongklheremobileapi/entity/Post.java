package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "posts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(generator = "uuid2")
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

    @ColumnDefault("0")
    private long likeCount;

    @ColumnDefault("0")
    private long favoriteCount;

    @ColumnDefault("0")
    private long commentCount;

    @ColumnDefault("0")
    private long viewCount;

    @ColumnDefault("false")
    private boolean isPublic;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Post(UUID id,
                SkinUrl skinUrl,
                StoryUrl storyUrl,
                SkinTime skinTime,
                SkinLocation skinLocation,
                long likeCount,
                long favoriteCount,
                long commentCount,
                long viewCount,
                boolean isPublic,
                LocalDateTime createdAt) {
        this.id = id;
        this.skinUrl = skinUrl;
        this.storyUrl = storyUrl;
        this.skinTime = skinTime;
        this.skinLocation = skinLocation;
        this.likeCount = likeCount;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
    }
}
