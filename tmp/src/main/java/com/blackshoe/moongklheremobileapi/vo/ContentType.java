package com.blackshoe.moongklheremobileapi.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    IMG_PNG("img/png"),
    IMG_JPEG("img/jpeg"),
    IMG_JPG("img/jpg"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_JPG("image/jpg");

    private final String contentType;

    public static boolean isContentTypeValid(String contentType) {
        for (ContentType type : ContentType.values()) {
            if (type.getContentType().equals(contentType)) {
                return true;
            }
        }
        return false;
    }
}
