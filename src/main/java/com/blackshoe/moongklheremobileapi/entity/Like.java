package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Like implements Persistable<LikePk> {
    @EmbeddedId
    private LikePk likePk = new LikePk();

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "like_fk_post_id"), referencedColumnName = "id")
    private Post post;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "like_fk_user_id"), referencedColumnName = "id")
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Like(Post post, User user, LocalDateTime createdAt) {
        this.post = post;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Post getPost() {
        return this.post;
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public LikePk getId() {
        return this.likePk;
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
