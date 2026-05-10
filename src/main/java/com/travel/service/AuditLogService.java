package com.travel.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 审计日志服务接口
 * 统一记录后台管理员的操作日志，供审计追溯使用
 */
public interface AuditLogService {

    /**
     * 记录审计日志
     * 自动从会话中提取当前管理员信息，组装日志实体后持久化
     *
     * @param session    HTTP会话（获取当前操作人）
     * @param request    HTTP请求（获取IP地址）
     * @param action     操作类型（LOGIN/LOGOUT/CREATE/UPDATE/DELETE/AUDIT）
     * @param targetType 操作对象类型（GUIDE/STORY/COMMENT/USER/ADMIN/DESTINATION等）
     * @param targetId   操作对象ID
     * @param detail     操作详情描述
     */
    void log(HttpSession session, HttpServletRequest request,
             String action, String targetType, Long targetId, String detail);
}
