package com.travel.service;

import com.travel.common.PageResult;
import com.travel.pojo.vo.GuideStoryVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * 旅行故事管理服务接口（管理员端）
 * 提供故事的 CRUD、审核等管理功能
 */
public interface AdminStoryService {

    /**
     * 分页查询故事列表
     *
     * @param page   页码
     * @param status 审核状态（可选）
     */
    PageResult<GuideStoryVO> listStories(int page, Integer status);

    /**
     * 获取故事详情
     *
     * @param id 故事ID
     */
    GuideStoryVO getStoryDetail(Long id);

    /**
     * 新建故事
     *
     * @param body    故事数据
     * @param session HTTP会话（获取当前操作人）
     * @param request HTTP请求（记录审计日志）
     */
    void createStory(Map<String, Object> body, HttpSession session, HttpServletRequest request);

    /**
     * 更新故事内容
     *
     * @param id      故事ID
     * @param body    更新的字段
     * @param session HTTP会话
     * @param request HTTP请求
     */
    void updateStory(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);

    /**
     * 审核故事
     *
     * @param id      故事ID
     * @param action  审核动作（approve/reject/down）
     * @param session HTTP会话
     * @param request HTTP请求
     */
    void auditStory(Long id, String action, HttpSession session, HttpServletRequest request);

    /**
     * 删除故事
     *
     * @param id      故事ID
     * @param session HTTP会话
     * @param request HTTP请求
     */
    void deleteStory(Long id, HttpSession session, HttpServletRequest request);
}
