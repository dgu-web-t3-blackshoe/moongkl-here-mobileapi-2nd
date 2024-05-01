package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_favorites")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Favorite {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",  foreignKey = @ForeignKey(name = "favorite_fk_post_id"), referencedColumnName = "id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "favorite_fk_user_id"), referencedColumnName = "id")
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public Favorite(Post post, User user, LocalDateTime createdAt) {
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
}
