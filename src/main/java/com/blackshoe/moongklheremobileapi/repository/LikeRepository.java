package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Like;
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
public interface LikeRepository extends JpaRepository<Like, UUID> {
    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "INNER JOIN Like l ON p.id = l.post.id " +
            "WHERE l.user = :user AND p.isPublic = true " +
            "ORDER BY l.createdAt DESC")
    Page<PostDto.PostListReadResponse> findAllLikedPostByUser(User user, Pageable pageable);

    @Query("SELECT l FROM Like l WHERE l.post = :post AND l.user = :user")
    Optional<Like> findByPostAndUser(Post post, User user);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.post = :post AND l.user = :user")
    boolean existsByPostAndUser(Post post, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.post = :post AND l.user = :user")
    void deleteByPostAndUser(Post post, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.user = :user")
    void deleteAllByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.post = :post")
    void deleteAllByPost(Post post);

    //findByUserId, count like rows by user id
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user.id = :userId")
    int countByUserId(UUID userId);

}
