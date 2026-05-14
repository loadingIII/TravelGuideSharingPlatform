package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 攻略评论 VO
 * 支持评论和回复的树形结构
 */
@Getter
@Builder
public class GuideCommentVO {
    private Long id;
    private Long guideId;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String content;
    private Long parentCommentId;
    private Integer likesCount;
    private LocalDateTime createdAt;

    /** 当前登录用户是否已点赞 */
    private boolean liked;

    /** 子回复列表（仅一级评论携带） */
    private List<GuideCommentVO> replies;
}
