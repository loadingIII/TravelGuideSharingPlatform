package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFollow {
    private Long followerUserId;
    private Long followedUserId;
    private LocalDateTime createdAt;
}
