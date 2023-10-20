package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode
public class FavoritePk implements Serializable {

    @Column(name = "post_id", columnDefinition = "BINARY(16)")
    private UUID postId;

    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    public FavoritePk(Post post, User user) {
        this.postId = post.getId();
        this.userId = user.getId();
    }

    public FavoritePk(UUID postId, UUID userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
