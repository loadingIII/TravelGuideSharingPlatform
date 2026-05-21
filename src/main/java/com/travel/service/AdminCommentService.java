package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.GuideComment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * 攻略评论管理服务接口（管理员端）
 * 提供攻略评论的分页查询、审核、删除等功能
 */
public interface AdminCommentService {

    /**
     * 分页查询攻略评论列表
     *
     * @param page   页码，从1开始
     * @param status 审核状态筛选（可选），null表示查询全部
     * @return 分页结果
     */
    PageResult<GuideComment> list(int page, Integer status);

    /**
     * 删除攻略评论
     *
     * @param id      评论ID
     * @param session HTTP会话
     * @param request HTTP请求
     */
    void delete(Long id, HttpSession session, HttpServletRequest request);

    /**
     * 审核攻略评论
     *
     * @param id      评论ID
     * @param body    含action字段（approve/reject/down）
     * @param session HTTP会话
     * @param request HTTP请求
     * @throws IllegalArgumentException 无效的审核操作
     */
    void audit(Long id, Map<String, String> body, HttpSession session, HttpServletRequest request);
}
