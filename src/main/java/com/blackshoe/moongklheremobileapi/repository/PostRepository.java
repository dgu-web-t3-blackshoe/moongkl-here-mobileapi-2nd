package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.vo.LocationType;
import com.blackshoe.moongklheremobileapi.vo.PostTimeFilter;
import com.blackshoe.moongklheremobileapi.vo.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE ((p.skinTime.year > :#{#postTimeFilter.fromYear}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.fromYear} AND p.skinTime.month > :#{#postTimeFilter.fromMonth}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.fromYear} AND p.skinTime.month = :#{#postTimeFilter.fromMonth} AND p.skinTime.day >= :#{#postTimeFilter.fromDay})) " +
            "  AND ((p.skinTime.year < :#{#postTimeFilter.toYear}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month < :#{#postTimeFilter.toMonth}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month = :#{#postTimeFilter.toMonth} AND p.skinTime.day <= :#{#postTimeFilter.toDay})) " +
            "  AND p.isPublic = true")
    Page<PostDto.PostListReadResponse> findAllBySkinTimeBetweenAndIsPublic(
            @Param("postTimeFilter") PostTimeFilter postTimeFilter,
            Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE ((p.skinTime.year > :#{#postTimeFilter.fromYear}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.fromYear} AND p.skinTime.month > :#{#postTimeFilter.fromMonth}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.fromYear} AND p.skinTime.month = :#{#postTimeFilter.fromMonth} AND p.skinTime.day >= :#{#postTimeFilter.fromDay})) " +
            "  AND ((p.skinTime.year < :#{#postTimeFilter.toYear}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month < :#{#postTimeFilter.toMonth}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month = :#{#postTimeFilter.toMonth} AND p.skinTime.day <= :#{#postTimeFilter.toDay})) " +
            "  AND p.isPublic = true" +
            "  AND p.skinLocation.country = '대한민국'")
    Page<PostDto.PostListReadResponse> findAllBySkinTimeBetweenAndDomesticAndIsPublic(PostTimeFilter postTimeFilter, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE ((p.skinTime.year > :#{#postTimeFilter.fromYear}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.fromYear} AND p.skinTime.month > :#{#postTimeFilter.fromMonth}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.fromYear} AND p.skinTime.month = :#{#postTimeFilter.fromMonth} AND p.skinTime.day >= :#{#postTimeFilter.fromDay})) " +
            "  AND ((p.skinTime.year < :#{#postTimeFilter.toYear}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month < :#{#postTimeFilter.toMonth}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month = :#{#postTimeFilter.toMonth} AND p.skinTime.day <= :#{#postTimeFilter.toDay})) " +
            "  AND p.isPublic = true" +
            "  AND p.skinLocation.country != '대한민국'")
    Page<PostDto.PostListReadResponse> findAllByCreatedAtBetweenAndAbroadAndIsPublic(PostTimeFilter postTimeFilter, Pageable pageable);
}
