package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.vo.PostAddressFilter;
import com.blackshoe.moongklheremobileapi.vo.PostPointFilter;
import com.blackshoe.moongklheremobileapi.vo.PostTimeFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    Page<PostDto.PostListReadResponse> findAllBySkinTimeBetweenAndIsPublic(PostTimeFilter postTimeFilter, Pageable pageable);

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
            "  AND (p.skinLocation.country = 'Republic of Korea' OR p.skinLocation.country = 'Korea' OR p.skinLocation.country = 'South Korea' OR p.skinLocation.country = '대한민국' OR p.skinLocation.country = '한국')")
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
            "  AND (p.skinLocation.country = 'Republic of Korea' OR p.skinLocation.country = 'Korea' OR p.skinLocation.country = 'South Korea' OR p.skinLocation.country = '대한민국' OR p.skinLocation.country = '한국')")
    Page<PostDto.PostListReadResponse> findAllBySkinTimeBetweenAndAbroadAndIsPublic(PostTimeFilter postTimeFilter, Pageable pageable);

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
            "  AND p.skinLocation.latitude BETWEEN :#{#postPointFilter.latitude - #postPointFilter.latitudeDelta} AND :#{#postPointFilter.latitude + #postPointFilter.latitudeDelta}" +
            "  AND p.skinLocation.longitude BETWEEN :#{#postPointFilter.longitude - #postPointFilter.longitudeDelta} AND :#{#postPointFilter.longitude + #postPointFilter.longitudeDelta}")
    Page<PostDto.PostListReadResponse> findAllBySkinTimeBetweenAndCurrentLocationAndIsPublic(PostTimeFilter postTimeFilter, PostPointFilter postPointFilter, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE p.user = :user " +
            "AND p.skinLocation.latitude BETWEEN :#{#postPointFilter.latitude - #postPointFilter.latitudeDelta} AND :#{#postPointFilter.latitude + #postPointFilter.latitudeDelta} " +
            "AND p.skinLocation.longitude BETWEEN :#{#postPointFilter.longitude - #postPointFilter.longitudeDelta} AND :#{#postPointFilter.longitude + #postPointFilter.longitudeDelta}")
    Page<PostDto.PostListReadResponse> findAllUserPostByLocation(User user, PostPointFilter postPointFilter, Pageable pageable);

    List<Post> findAllByUser(User user);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostGroupByCityReadResponse(" +
            "p.skinLocation.country, p.skinLocation.state, p.skinLocation.city, COUNT(p), p.skinUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE p.user = :user " +
            "AND p.skinLocation.latitude BETWEEN :#{#postPointFilter.latitude - #postPointFilter.latitudeDelta} AND :#{#postPointFilter.latitude + #postPointFilter.latitudeDelta} " +
            "AND p.skinLocation.longitude BETWEEN :#{#postPointFilter.longitude - #postPointFilter.longitudeDelta} AND :#{#postPointFilter.longitude + #postPointFilter.longitudeDelta} " +
            "GROUP BY p.skinLocation.country, p.skinLocation.state, p.skinLocation.city "+
            "ORDER BY COUNT(p) DESC")
    Page<PostDto.PostGroupByCityReadResponse> findAllUserPostByLocationAndGroupByCity(User user, PostPointFilter postPointFilter, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE p.user = :user " +
            "AND p.skinLocation.country = :#{#postAddressFilter.country} " +
            "AND p.skinLocation.state = :#{#postAddressFilter.state} " +
            "AND p.skinLocation.city = :#{#postAddressFilter.city} " )
    Page<PostDto.PostListReadResponse> findAllUserPostByCity(User user, PostAddressFilter postAddressFilter, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE p.user = :user " +
            "  AND ((p.skinTime.year < :#{#postTimeFilter.toYear}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month < :#{#postTimeFilter.toMonth}) OR " +
            "       (p.skinTime.year = :#{#postTimeFilter.toYear} AND p.skinTime.month = :#{#postTimeFilter.toMonth} AND p.skinTime.day <= :#{#postTimeFilter.toDay})) " )
    Page<PostDto.PostListReadResponse> findAllUserPostBySkinTime(User user, PostTimeFilter postTimeFilter, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE p.user.id = :userId " +
            "AND p.isPublic = true")
    Page<PostDto.PostListReadResponse> findAllPublicUserPost(UUID userId, Pageable pageable);

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.PostDto$PostListReadResponse(" +
            "p.id, p.user.id, p.skinUrl.cloudfrontUrl, p.storyUrl.cloudfrontUrl) " +
            "FROM Post p " +
            "WHERE p.user = :user " )
    Page<PostDto.PostListReadResponse> findAllUserPost(User user, Pageable pageable);
}
