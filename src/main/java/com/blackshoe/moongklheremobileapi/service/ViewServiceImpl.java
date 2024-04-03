package com.blackshoe.moongklheremobileapi.service;

import com.blackshoe.moongklheremobileapi.dto.MessageDto;
import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.entity.Post;
import com.blackshoe.moongklheremobileapi.entity.StoryUrl;
import com.blackshoe.moongklheremobileapi.entity.User;
import com.blackshoe.moongklheremobileapi.entity.View;
import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import com.blackshoe.moongklheremobileapi.repository.PostRepository;
import com.blackshoe.moongklheremobileapi.repository.ViewRepository;
import com.blackshoe.moongklheremobileapi.sqs.SqsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ViewServiceImpl implements ViewService {

    private final ViewRepository viewRepository;

    private final PostRepository postRepository;

    private final SqsSender sqsSender;

    public ViewServiceImpl(ViewRepository viewRepository, PostRepository postRepository, SqsSender sqsSender) {
        this.viewRepository = viewRepository;
        this.postRepository = postRepository;
        this.sqsSender = sqsSender;
    }

    @Override
    @Transactional
    public PostDto.IncreaseViewCountDto increaseViewCount(UUID postId, User user) {

        final Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        final StoryUrl storyUrl = post.getStoryUrl();

        if (storyUrl.getEnterprise() != null) {
            Map<String, String> messageMap = new LinkedHashMap<>();
            messageMap.put("id", storyUrl.getId().toString());

            MessageDto messageDto = sqsSender.createMessageDtoFromRequest("increase view count", messageMap);

            sqsSender.sendToSQS(messageDto);
        }

        final Optional<View> optionalView = viewRepository.findByPostAndUser(post, user);

        if (optionalView.isEmpty()) {
            post.increaseViewCount();

            final View newView = View.builder()
                    .post(post)
                    .user(user)
                    .build();

            final View savedView = viewRepository.save(newView);

            final PostDto.IncreaseViewCountDto increaseViewCountDto = PostDto.IncreaseViewCountDto.builder()
                    .postId(post.getId())
                    .viewCount(post.getViewCount())
                    .userId(user.getId())
                    .lastViewedAt(savedView.getLastViewedAt())
                    .build();

            return increaseViewCountDto;
        }

        final View view = optionalView.get();

        final LocalDateTime lastViewAt = view.getLastViewedAt();
        final LocalDateTime now = LocalDateTime.now();

        if (lastViewAt.plusDays(1).isBefore(now)) {
            post.increaseViewCount();
            view.updateLastViewedAt(now);
        }

        final PostDto.IncreaseViewCountDto increaseViewCountDto = PostDto.IncreaseViewCountDto.builder()
                .postId(post.getId())
                .viewCount(post.getViewCount())
                .userId(user.getId())
                .lastViewedAt(view.getLastViewedAt())
                .build();

        return increaseViewCountDto;
    }
}
