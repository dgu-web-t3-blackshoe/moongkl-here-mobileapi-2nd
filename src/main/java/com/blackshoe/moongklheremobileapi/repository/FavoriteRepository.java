package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Favorite;
import com.blackshoe.moongklheremobileapi.entity.FavoritePk;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoritePk> {
    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "INNER JOIN Favorite f ON p.id = f.post.id " +
            "WHERE f.user = :user AND p.isPublic = true " +
            "ORDER BY f.createdAt DESC")
    Page<PostDto.PostListReadResponse> findAllFavoritePostByUser(User user, Pageable pageable);
}
