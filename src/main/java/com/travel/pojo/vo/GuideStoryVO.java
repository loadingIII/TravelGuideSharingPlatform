package com.travel.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideStoryVO {
    private Long id;
    private Long authorUserId;
    private String authorName;
    private String authorAvatarUrl;
    private Boolean isVip;
    private String content;
    private List<String> images;
    private LocalDateTime publishedAt;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private LocalDateTime createdAt;
    private Integer status;
}
