package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoryLike {
    private Long storyId;
    private Long userId;
    private LocalDateTime createdAt;
}
