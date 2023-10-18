package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Like;
import com.blackshoe.moongklheremobileapi.entity.LikePk;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikePk> {
    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "INNER JOIN Like l ON p.id = l.likePk.post.id " +
            "WHERE l.likePk.user = :user AND p.isPublic = true " +
            "ORDER BY l.createdAt DESC")
    Page<PostDto.PostListReadResponse> findAllLikedPostByUser(User user, Pageable pageable);
}
