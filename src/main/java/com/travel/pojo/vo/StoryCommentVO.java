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
public class StoryCommentVO {
    private Long id;
    private Long storyId;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String content;
    private Long parentCommentId;
    private Integer likesCount;
    private LocalDateTime createdAt;
    private Boolean liked;
    private List<StoryCommentVO> replies;
}
