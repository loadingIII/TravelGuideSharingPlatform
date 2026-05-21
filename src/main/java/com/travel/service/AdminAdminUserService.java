package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.AdminUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * 管理员账户管理服务接口
 * 提供后台管理员的 CRUD、密码加密、唯一性校验、自删保护等功能
 */
public interface AdminAdminUserService {

    /**
     * 分页查询管理员列表
     *
     * @param page 页码，从1开始
     * @return 分页结果
     */
    PageResult<AdminUser> list(int page);

    /**
     * 获取管理员详情（密码哈希会被清空后返回）
     *
     * @param id 管理员ID
     * @return 管理员信息，不存在返回null
     */
    AdminUser detail(Long id);

    /**
     * 新建管理员
     * 校验用户名唯一性，对密码进行BCrypt加密后入库
     *
     * @param body    含username、password、realName
     * @param session HTTP会话（获取当前操作人）
     * @param request HTTP请求（记录审计日志）
     * @throws IllegalArgumentException 用户名或密码为空
     * @throws IllegalStateException    用户名已存在
     */
    void create(Map<String, Object> body, HttpSession session, HttpServletRequest request);

    /**
     * 修改管理员信息
     * 支持修改姓名、状态，密码非空时同时更新密码
     *
     * @param id      管理员ID
     * @param body    含realName、status、password（可选）
     * @param session HTTP会话
     * @param request HTTP请求
     * @throws IllegalArgumentException 管理员不存在
     */
    void update(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);

    /**
     * 删除管理员
     * 不允许删除当前登录的管理员自身
     *
     * @param id      管理员ID
     * @param session HTTP会话
     * @param request HTTP请求
     * @throws IllegalArgumentException 管理员不存在
     * @throws IllegalStateException    试图删除自己
     */
    void delete(Long id, HttpSession session, HttpServletRequest request);
}
