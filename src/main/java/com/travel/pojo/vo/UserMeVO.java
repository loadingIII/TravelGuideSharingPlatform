package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMeVO {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String bio;
    private Boolean vip;
    private Integer guidesCount;
    private Integer followersCount;
    private Integer likesReceivedCount;
}
