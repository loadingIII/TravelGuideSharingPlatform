package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfile {
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String bio;
    private Boolean isVip;
    private Integer guidesCount;
    private Integer followersCount;
    private Integer likesReceivedCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
