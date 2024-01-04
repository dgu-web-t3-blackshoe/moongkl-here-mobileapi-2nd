package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Favorite;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.vo.PostAddressFilter;
import com.blackshoe.moongklheremobileapi.vo.PostPointFilter;
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

    //findByUserId, count favorite rows by user id
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId")
    int countByUserId(UUID userId);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostGroupByCityReadResponse(" +
            "f.post.skinLocation.country, f.post.skinLocation.state, f.post.skinLocation.city, COUNT(f.post), f.post.skinUrl.cloudfrontUrl) " +
            "FROM Favorite f " +
            "WHERE f.user = :user " +
            "AND f.post.skinLocation.latitude BETWEEN :#{#postPointFilter.latitude} - :#{#postPointFilter.radius} " +
            "AND :#{#postPointFilter.latitude} + :#{#postPointFilter.radius} " +
            "GROUP BY f.post.skinLocation.country, f.post.skinLocation.state, f.post.skinLocation.city "+
            "ORDER BY COUNT(f.post) DESC")
    Page<PostDto.PostGroupByCityReadResponse> findAllUserFavoritePostByLocationAndGroupByCity(User user, PostPointFilter postPointFilter, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "f.post.id, f.post.id, f.post.skinUrl.cloudfrontUrl, f.post.storyUrl.cloudfrontUrl) " +
            "FROM Favorite f " +
            "WHERE f.user = :user " +
            "AND f.post.skinLocation.country = :#{#postAddressFilter.country} " +
            "AND f.post.skinLocation.state = :#{#postAddressFilter.state} " +
            "AND f.post.skinLocation.city = :#{#postAddressFilter.city} " )
    Page<PostDto.PostListReadResponse> findAllUserFavoritePostByCity(User user, PostAddressFilter postAddressFilter, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostWithDateListReadResponse(" +
            "f.post.id, f.post.id, f.post.skinUrl.cloudfrontUrl, f.post.storyUrl.cloudfrontUrl, f.post.skinTime.year, f.post.skinTime.month, f.post.skinTime.day) " +
            "FROM Favorite f " +
            "WHERE f.user = :user " +
            "ORDER BY f.post.createdAt DESC")
    Page<PostDto.PostWithDateListReadResponse> findAllUserFavoritePostByUser(User user, Pageable pageable);
}
