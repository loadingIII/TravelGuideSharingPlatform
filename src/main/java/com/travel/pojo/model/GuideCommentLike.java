package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideCommentLike {
    private Long commentId;
    private Long userId;
    private LocalDateTime createdAt;
}
