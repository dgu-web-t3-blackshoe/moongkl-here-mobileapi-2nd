package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Favorite;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "INNER JOIN Favorite f ON p.id = f.post.id " +
            "WHERE f.user = :user AND p.isPublic = true " +
            "ORDER BY f.createdAt DESC")
    Page<PostDto.PostListReadResponse> findAllFavoritePostByUser(User user, Pageable pageable);

    @Query("SELECT f FROM Favorite f WHERE f.post = :post AND f.user = :user")
    Optional<Favorite> findByPostAndUser(Post post, User user);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorite f WHERE f.post = :post AND f.user = :user")
    boolean existsByPostAndUser(Post post, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.post = :post AND f.user = :user")
    void deleteByPostAndUser(Post post, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.user = :user")
    void deleteAllByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.post = :post")
    void deleteAllByPost(Post post);
}
