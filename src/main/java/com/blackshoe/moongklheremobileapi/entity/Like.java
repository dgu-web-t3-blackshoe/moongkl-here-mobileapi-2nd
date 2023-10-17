package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Like {
    @EmbeddedId
    private LikePk likePk;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Like(Post post, User user, LocalDateTime createdAt) {
        this.likePk = LikePk.builder().post(post).user(user).build();
        this.createdAt = createdAt;
    }

    public Post getPost() {
        return this.likePk.getPost();
    }

    public User getUser() {
        return this.likePk.getUser();
    }
}
