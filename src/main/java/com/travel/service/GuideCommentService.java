package com.travel.service;

import com.travel.pojo.vo.GuideCommentVO;

import java.util.List;

/**
 * 攻略评论服务接口
 * 处理攻略评论的查询逻辑
 */
public interface GuideCommentService {

    /**
     * 获取攻略的评论列表（树形结构）
     * 一级评论按时间正序，每条一级评论下挂载其回复
     *
     * @param guideId 攻略ID
     * @return 评论列表（一级评论 + 嵌套回复）
     */
    List<GuideCommentVO> listCommentsByGuideId(Long guideId);
}
