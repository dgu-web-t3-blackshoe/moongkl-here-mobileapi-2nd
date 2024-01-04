package com.blackshoe.moongklheremobileapi.vo;

import java.sql.Timestamp;

public class UserVo {

    //user_id, email, password, nickname, phone, like_count, favorite_count, created_at, updated_at, last_login
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private Long likeCount;
    private Long favoriteCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp lastLogin;
}
