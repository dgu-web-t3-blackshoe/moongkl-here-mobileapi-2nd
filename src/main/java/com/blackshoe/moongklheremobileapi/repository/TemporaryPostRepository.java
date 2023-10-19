package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.TemporaryPostDto;
import com.blackshoe.moongklheremobileapi.entity.TemporaryPost;
import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemporaryPostRepository extends JpaRepository<TemporaryPost, UUID> {
    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.TemporaryPostDto$TemporaryPostListReadResponse(" +
            "tp.id, " +
            "tp.user.id, " +
            "tp.skinUrl.cloudfrontUrl, " +
            "tp.storyUrl.cloudfrontUrl) " +
            "FROM TemporaryPost tp " +
            "WHERE tp.user = :user")
    Page<TemporaryPostDto.TemporaryPostListReadResponse> findAllByUser(User user, Pageable pageable);
}
