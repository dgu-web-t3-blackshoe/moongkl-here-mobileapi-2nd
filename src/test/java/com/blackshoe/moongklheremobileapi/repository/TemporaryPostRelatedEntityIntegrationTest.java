package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.TemporaryPostDto;
import com.blackshoe.moongklheremobileapi.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TemporaryPostRelatedEntityIntegrationTest {

    @Autowired
    private TemporaryPostRepository temporaryPostRepository;

    @Autowired
    private SkinUrlRepository skinUrlRepository;

    @Autowired
    private StoryUrlRepository storyUrlRepository;

    @Autowired
    private SkinLocationRepository skinLocationRepository;

    @Autowired
    private SkinTimeRepository skinTimeRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        final User user = User.builder()
                .email("test")
                .password("test")
                .nickname("test")
                .phoneNumber("test")
                .build();

        userRepository.save(user);
    }

    @Test
    public void temporaryPostDelete_whenSuccess_relatedAlsoDeleted() {
        //given
        final User foundUser = userRepository.findByEmail("test").get();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final TemporaryPost temporaryPost = TemporaryPost.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .user(foundUser)
                .build();

        final TemporaryPost savedTemporaryPost = temporaryPostRepository.save(temporaryPost);

        final UUID temporaryPostId = savedTemporaryPost.getId();
        final UUID skinUrlId = savedTemporaryPost.getSkinUrl().getId();
        final UUID storyUrlId = savedTemporaryPost.getStoryUrl().getId();
        final UUID skinTimeId = savedTemporaryPost.getSkinTime().getId();
        final UUID skinLocationId = savedTemporaryPost.getSkinLocation().getId();

        //when
        temporaryPostRepository.delete(savedTemporaryPost);

        //then
        assertThat(temporaryPostRepository.findById(temporaryPostId)).isEmpty();
        assertThat(skinUrlRepository.findById(skinUrlId)).isEmpty();
        assertThat(storyUrlRepository.findById(storyUrlId)).isEmpty();
        assertThat(skinTimeRepository.findById(skinTimeId)).isEmpty();
        assertThat(skinLocationRepository.findById(skinLocationId)).isEmpty();
        assertThat(userRepository.findByEmail("test")).isNotEmpty();
    }

    @Test
    public void userDelete_whenSuccess_relatedAlsoDeleted() {
        //given
        final User foundUser = userRepository.findByEmail("test").get();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final TemporaryPost temporaryPost = TemporaryPost.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .build();

        temporaryPost.setUser(foundUser);

        final TemporaryPost savedTemporaryPost = temporaryPostRepository.save(temporaryPost);

        final UUID temporaryPostId = savedTemporaryPost.getId();
        final UUID skinUrlId = savedTemporaryPost.getSkinUrl().getId();
        final UUID storyUrlId = savedTemporaryPost.getStoryUrl().getId();
        final UUID skinTimeId = savedTemporaryPost.getSkinTime().getId();
        final UUID skinLocationId = savedTemporaryPost.getSkinLocation().getId();

        //when
        userRepository.delete(foundUser);

        //then
        assertThat(userRepository.findByEmail("test")).isEmpty();
        assertThat(temporaryPostRepository.findById(temporaryPostId)).isEmpty();
        assertThat(skinUrlRepository.findById(skinUrlId)).isEmpty();
        assertThat(storyUrlRepository.findById(storyUrlId)).isEmpty();
        assertThat(skinTimeRepository.findById(skinTimeId)).isEmpty();
        assertThat(skinLocationRepository.findById(skinLocationId)).isEmpty();
    }

    @Test
    public void findByUser_returns_userSavedTemporaryPosts() {
        //given
        final User foundUser = userRepository.findByEmail("test").get();

        final SkinUrl skinUrl = SkinUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final StoryUrl storyUrl = StoryUrl.builder()
                .s3Url("test")
                .cloudfrontUrl("test")
                .build();

        final SkinTime skinTime = SkinTime.builder()
                .year(2021)
                .month(1)
                .day(1)
                .hour(1)
                .minute(1)
                .build();

        final SkinLocation skinLocation = SkinLocation.builder()
                .latitude(1.0)
                .longitude(1.0)
                .country("test")
                .state("test")
                .city("test")
                .build();

        final TemporaryPost temporaryPost = TemporaryPost.builder()
                .skinUrl(skinUrl)
                .storyUrl(storyUrl)
                .skinTime(skinTime)
                .skinLocation(skinLocation)
                .build();

        temporaryPost.setUser(foundUser);

        final TemporaryPost savedTemporaryPost = temporaryPostRepository.save(temporaryPost);

        final Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Integer size = 10;
        final Integer page = 0;
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        //when
        final Page<TemporaryPostDto.TemporaryPostListReadResponse> foundTemporaryPostPage
                = temporaryPostRepository.findAllByUser(savedTemporaryPost.getUser(), pageable);

        //then
        assertThat(foundTemporaryPostPage).isNotNull();
        assertThat(foundTemporaryPostPage.getContent().size()).isEqualTo(1);
    }

}
