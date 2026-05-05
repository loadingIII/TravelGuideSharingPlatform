package com.travel.service;

import com.travel.pojo.model.AdminUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * 管理员认证服务接口
 * 处理管理员登录、登出、会话管理等认证相关的业务逻辑
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     * 验证用户名和密码，成功后更新最后登录时间并设置会话
     *
     * @param username 用户名
     * @param password 密码
     * @param session HTTP会话
     * @return 登录是否成功
     */
    boolean login(String username, String password, HttpSession session);

    /**
     * 设置会话信息
     * 将管理员信息存入会话
     *
     * @param session HTTP会话
     * @param admin 管理员信息
     */
    void setSession(HttpSession session, AdminUser admin);

    /**
     * 管理员登出
     * 清除会话信息和记住我Cookie
     *
     * @param session HTTP会话
     * @param response HTTP响应
     */
    void logout(HttpSession session, HttpServletResponse response);

    /**
     * 检查是否已登录
     *
     * @param session HTTP会话
     * @return 是否已登录
     */
    boolean isLoggedIn(HttpSession session);

    /**
     * 获取当前登录的管理员信息
     *
     * @param session HTTP会话
     * @return 管理员信息Map，未登录返回null
     */
    @SuppressWarnings("unchecked")
    Map<String, Object> getCurrentAdmin(HttpSession session);

    /**
     * 设置记住我Cookie
     * 生成签名令牌并设置到响应中
     *
     * @param response HTTP响应
     * @param adminId 管理员ID
     */
    void setRememberCookie(HttpServletResponse response, Long adminId);

    /**
     * 尝试通过记住我Cookie恢复会话
     * 验证Cookie有效性，成功则恢复会话
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param session HTTP会话
     * @return 是否恢复成功
     */
    boolean tryRestoreSession(HttpServletRequest request, HttpServletResponse response, HttpSession session);

    /**
     * 清除记住我Cookie
     *
     * @param response HTTP响应
     */
    void clearRememberCookie(HttpServletResponse response);
}
