package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅行者故事VO
 */
@Data
@Builder
public class GuideStoryVO {
    private Long id;
    private Long authorUserId;
    private String authorName;
    private String authorAvatarUrl;
    private Boolean isVip;
    private String content;
    private LocalDateTime publishedAt;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private LocalDateTime createdAt;
}
