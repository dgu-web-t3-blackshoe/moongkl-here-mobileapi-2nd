package com.blackshoe.moongklheremobileapi.entity;

import com.blackshoe.moongklheremobileapi.security.EncryptionConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Convert(converter = EncryptionConverter.class)
    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Column(name = "provider", length = 20)
    private String provider;

    @CreatedDate
    @Column(name = "created_at", nullable = false, length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TemporaryPost> temporaryPosts;

    @JoinColumn(name = "profile_img_url_id", foreignKey = @ForeignKey(name = "user_fk_profile_img_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ProfileImgUrl profileImgUrl;

    @JoinColumn(name = "background_img_url_id", foreignKey = @ForeignKey(name = "user_fk_background_img_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BackgroundImgUrl backgroundImgUrl;

    @Column(name = "status_message", length = 100)
    private String statusMessage;

    @Column(name = "like_count", length = 10)
    private int likeCount;

    @Column(name = "favorite_count", length = 10)
    private int favoriteCount;

    public void setProvider(String authProvider) {
        this.provider = authProvider;
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }

    public void addTemporaryPost(TemporaryPost temporaryPost) {
        this.temporaryPosts.add(temporaryPost);
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder(toBuilder = true)
    public User(UUID id,
                String email,
                String password,
                String nickname,
                String phoneNumber,
                String gender,
                String country,
                Role role,
                String provider,
                ProfileImgUrl profileImgUrl,
                BackgroundImgUrl backgroundImgUrl,
                String statusMessage,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.country = country;
        this.role = role;
        this.provider = provider;
        this.profileImgUrl = profileImgUrl;
        this.backgroundImgUrl = backgroundImgUrl;
        this.statusMessage = statusMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.posts = new ArrayList<>();
        this.temporaryPosts = new ArrayList<>();
    }

    public void setProfileImgUrl(ProfileImgUrl profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void setBackgroundImgUrl(BackgroundImgUrl backgroundImgUrl) {
        this.backgroundImgUrl = backgroundImgUrl;
    }
}
