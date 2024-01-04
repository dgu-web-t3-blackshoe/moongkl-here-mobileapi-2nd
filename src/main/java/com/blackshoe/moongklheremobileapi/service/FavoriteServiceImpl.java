package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Favorite;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.exception.InteractionErrorResult;
import com.blackshoe.moongklheremobileapi.exception.InteractionException;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.FavoriteRepository;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.blackshoe.moongklheremobileapi.vo.PostAddressFilter;
import com.blackshoe.moongklheremobileapi.vo.PostPointFilter;
import com.blackshoe.moongklheremobileapi.vo.SortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final PostRepository postRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, PostRepository postRepository) {
        this.favoriteRepository = favoriteRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public PostDto.FavoritePostDto favoritePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        if (favoriteRepository.existsByPostAndUser(post, user)) {
            throw new InteractionException(InteractionErrorResult.FAVORITE_ALREADY_EXIST);
        }

        final Favorite favorite = Favorite.builder()
                .post(post)
                .user(user)
                .build();

        final Favorite savedFavorite = favoriteRepository.save(favorite);

        post.increaseFavoriteCount();

        final PostDto.FavoritePostDto favoritePostDto = PostDto.FavoritePostDto.builder()
                .postId(post.getId())
                .favoriteCount(post.getFavoriteCount())
                .userId(user.getId())
                .createdAt(savedFavorite.getCreatedAt())
                .build();

        return favoritePostDto;
    }

    @Override
    @Transactional
    public PostDto.DeleteFavoritePostDto deleteFavoritePost(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final Favorite favorite = favoriteRepository.findByPostAndUser(post, user).orElseThrow(() -> new InteractionException(InteractionErrorResult.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);

        post.decreaseFavoriteCount();

        final PostDto.DeleteFavoritePostDto deleteFavoritePostDto = PostDto.DeleteFavoritePostDto.builder()
                .postId(post.getId())
                .favoriteCount(post.getFavoriteCount())
                .userId(user.getId())
                .deletedAt(LocalDateTime.now())
                .build();

        return deleteFavoritePostDto;
    }

    @Override
    public Page<PostDto.PostListReadResponse> getUserFavoritePostList(User user, Integer size, Integer page) {

        final Pageable pageable = PageRequest.of(page, size);

        final Page<PostDto.PostListReadResponse> userFavoritePostResponse
                = favoriteRepository.findAllFavoritePostByUser(user, pageable);

        return userFavoritePostResponse;
    }

    @Override
    public PostDto.DidUserFavoritePostResponse didUserFavoritePost(User user, UUID postId) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final boolean didUserFavoritePost = favoriteRepository.existsByPostAndUser(post, user);

        final PostDto.DidUserFavoritePostResponse didUserFavoritePostResponse = PostDto.DidUserFavoritePostResponse.builder()
                .isTrue(didUserFavoritePost)
                .build();

        return didUserFavoritePostResponse;
    }

    @Override
    public Page<PostDto.PostGroupByCityReadResponse> getUserFavoritePostListGroupedByCity(User user,
                                                                                          Double latitude,
                                                                                          Double longitude,
                                                                                          Double radius,
                                                                                          Integer size,
                                                                                          Integer page) {

        final PostPointFilter postPointFilter = PostPointFilter.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .build();

        final Pageable pageable = PageRequest.of(page, size);

        final Page<PostDto.PostGroupByCityReadResponse> postGroupByCityReadResponsePage
                = favoriteRepository.findAllUserFavoritePostByLocationAndGroupByCity(user, postPointFilter, pageable);

        return postGroupByCityReadResponsePage;
    }

    @Override
    public Page<PostDto.PostListReadResponse> getUserCityFavoritePostList(User user,
                                                                          String country,
                                                                          String state,
                                                                          String city,
                                                                          String sort,
                                                                          Integer size,
                                                                          Integer page) {

        final PostAddressFilter postAddressFilter = PostAddressFilter.builder()
                .country(country)
                .state(state)
                .city(city)
                .build();

        final SortType sortType = SortType.verifyAndConvertStringToSortType(sort);

        final Sort sortBy = Sort.by(Sort.Direction.DESC, SortType.getSortField(sortType));

        final Pageable pageable = PageRequest.of(page, size, sortBy);

        final Page<PostDto.PostListReadResponse> userCityFavoritePostReadResponsePage
                = favoriteRepository.findAllUserFavoritePostByCity(user, postAddressFilter, pageable);

        return userCityFavoritePostReadResponsePage;
    }

    @Override
    public Page<PostDto.PostWithDateListReadResponse> getUserFavoritePostWithDateList(User user, Integer size, Integer page) {

        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");

        final Pageable pageable = PageRequest.of(0, 10, sortBy);

        final Page<PostDto.PostWithDateListReadResponse> userFavoritePostWithDateListResponsePage
                = favoriteRepository.findAllUserFavoritePostByUser(user, pageable);

        return userFavoritePostWithDateListResponsePage;
    }
}
