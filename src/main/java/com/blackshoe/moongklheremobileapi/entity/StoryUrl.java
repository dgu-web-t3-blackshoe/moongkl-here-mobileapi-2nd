package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "story_urls")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class StoryUrl {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private String s3Url;

    private String cloudfrontUrl;

    @Builder
    public StoryUrl(UUID id, String s3Url, String cloudfrontUrl) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
    }
}
