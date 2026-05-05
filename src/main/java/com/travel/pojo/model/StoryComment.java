package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoryComment {
    private Long id;
    private Long storyId;
    private Long userId;
    private String content;
    private Long parentCommentId;
    private Integer likesCount;
    private Integer status;
    private LocalDateTime createdAt;

    /** 评论者昵称（关联查询） */
    private String authorName;
    /** 评论者头像（关联查询） */
    private String authorAvatarUrl;
}
