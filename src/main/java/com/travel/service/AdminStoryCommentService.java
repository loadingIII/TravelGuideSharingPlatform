package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.StoryComment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * 故事评论管理服务接口（管理员端）
 * 提供旅行故事评论的分页查询、审核、删除等功能
 */
public interface AdminStoryCommentService {

    /**
     * 分页查询故事评论列表
     *
     * @param page   页码，从1开始
     * @param status 审核状态筛选（可选），null表示查询全部
     * @return 分页结果
     */
    PageResult<StoryComment> list(int page, Integer status);

    /**
     * 删除故事评论
     *
     * @param id      评论ID
     * @param session HTTP会话
     * @param request HTTP请求
     */
    void delete(Long id, HttpSession session, HttpServletRequest request);

    /**
     * 审核故事评论
     *
     * @param id      评论ID
     * @param body    含action字段（approve/reject/down）
     * @param session HTTP会话
     * @param request HTTP请求
     * @throws IllegalArgumentException 无效的审核操作
     */
    void audit(Long id, Map<String, String> body, HttpSession session, HttpServletRequest request);
}
