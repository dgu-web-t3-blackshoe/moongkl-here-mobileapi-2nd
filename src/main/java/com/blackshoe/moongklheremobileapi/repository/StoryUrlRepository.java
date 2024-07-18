package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Enterprise;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoryUrlRepository extends JpaRepository<StoryUrl, UUID> {

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$EnterpriseStoryReadResponse " +
            "(s.id, e.id, s.cloudfrontUrl, s.createdAt) " +
            "FROM StoryUrl s JOIN s.enterprise e " +
            "WHERE s.isPublic = TRUE")
    Page<PostDto.EnterpriseStoryReadResponse> findAllEnterpriseStory(Pageable pageable);

    List<StoryUrl> findByEnterpriseOrderByCreatedAtDesc(Enterprise enterprise);

}
