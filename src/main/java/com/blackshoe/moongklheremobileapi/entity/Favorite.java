package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_favorites")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Favorite {
    @EmbeddedId
    private FavoritePk favoritePk;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Favorite(Post post, User user, LocalDateTime createdAt) {
        this.favoritePk = FavoritePk.builder().post(post).user(user).build();
        this.createdAt = createdAt;
    }

    public Post getPost() {
        return this.favoritePk.getPost();
    }

    public User getUser() {
        return this.favoritePk.getUser();
    }
}
