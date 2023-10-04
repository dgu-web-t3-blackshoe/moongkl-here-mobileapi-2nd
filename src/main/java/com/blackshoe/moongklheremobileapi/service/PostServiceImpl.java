package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.blackshoe.moongklheremobileapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public Post createPost(User user, SkinUrl skinUrl, StoryUrl storyUrl, PostDto.PostCreateRequest postCreateRequest) {

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(postCreateRequest.getLocation().getLatitude())
                .longitude(postCreateRequest.getLocation().getLongitude())
                .country(postCreateRequest.getLocation().getCountry())
                .state(postCreateRequest.getLocation().getState())
                .city(postCreateRequest.getLocation().getCity())
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(postCreateRequest.getTime().getYear())
                .month(postCreateRequest.getTime().getMonth())
                .day(postCreateRequest.getTime().getDay())
                .hour(postCreateRequest.getTime().getHour())
                .minute(postCreateRequest.getTime().getMinute())
                .build();

        final Post post = Post.builder()
                .user(user)
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinLocation(skinLocation)
                .skinTime(skinTime)
                .isPublic(postCreateRequest.getIsPublic())
                .build();


        postRepository.save(post);

        return post;
    }
}
