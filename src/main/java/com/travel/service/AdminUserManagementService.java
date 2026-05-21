package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * 普通用户管理服务接口（管理员端）
 * 提供前台注册用户的 CRUD、审核、状态管理等功能
 */
public interface AdminUserManagementService {

    /**
     * 分页查询用户列表
     *
     * @param page   页码，从1开始
     * @param status 用户状态筛选（可选），null表示查询全部
     * @return 分页结果
     */
    PageResult<User> list(int page, Integer status);

    /**
     * 获取用户详情（含用户画像信息）
     * 将 User 和 UserProfile 合并为一个 Map 返回
     *
     * @param id 用户ID
     * @return 用户信息Map，不存在返回null
     */
    Map<String, Object> detail(Long id);

    /**
     * 编辑用户信息
     *
     * @param id      用户ID
     * @param body    含username、phone、email、status
     * @param session HTTP会话
     * @param request HTTP请求
     * @throws IllegalArgumentException 用户不存在
     */
    void update(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);

    /**
     * 审核用户状态（禁用/启用）
     *
     * @param id      用户ID
     * @param action  审核动作（disable/enable）
     * @param session HTTP会话
     * @param request HTTP请求
     * @throws IllegalArgumentException 用户不存在或操作无效
     */
    void audit(Long id, String action, HttpSession session, HttpServletRequest request);

    /**
     * 删除用户
     *
     * @param id      用户ID
     * @param session HTTP会话
     * @param request HTTP请求
     */
    void delete(Long id, HttpSession session, HttpServletRequest request);
}
