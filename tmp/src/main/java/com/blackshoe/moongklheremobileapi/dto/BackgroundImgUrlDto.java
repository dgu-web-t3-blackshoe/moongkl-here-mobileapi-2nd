package com.blackshoe.moongklheremobileapi.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BackgroundImgUrlDto {
    private String s3Url;
    private String cloudfrontUrl;
}
