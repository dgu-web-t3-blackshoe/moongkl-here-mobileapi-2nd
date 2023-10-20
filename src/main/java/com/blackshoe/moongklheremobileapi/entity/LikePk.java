package com.blackshoe.moongklheremobileapi.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode
public class LikePk implements Serializable {
    @Column(name = "post_id", columnDefinition = "BINARY(16)")
    private UUID postId;

    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    public LikePk(Post post, User user) {
        this.postId = post.getId();
        this.userId = user.getId();
    }

    public LikePk(UUID postId, UUID userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
