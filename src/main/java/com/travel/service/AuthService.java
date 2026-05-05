package com.travel.service;


import com.travel.pojo.dto.AuthLoginDTO;
import com.travel.pojo.dto.AuthRegisterDTO;
import com.travel.pojo.vo.AuthTokenVO;

/**
 * 认证服务接口
 * 处理用户注册、登录、刷新令牌等认证相关的业务逻辑
 */
public interface AuthService {

    /**
     * 用户注册
     * 创建新用户账户并返回访问令牌
     *
     * @param request 注册请求，包含用户名、密码、手机号等信息
     * @return 访问令牌响应
     */
    AuthTokenVO register(AuthRegisterDTO request);

    /**
     * 用户登录
     * 验证用户凭证并返回访问令牌
     *
     * @param request 登录请求，包含手机号和密码
     * @return 访问令牌响应
     */
    AuthTokenVO login(AuthLoginDTO request);

    /**
     * 刷新访问令牌
     * 使用有效的刷新令牌获取新的访问令牌
     *
     * @param authorization 请求头中的授权信息（Bearer token）
     * @return 新的访问令牌响应
     */
    AuthTokenVO refresh(String authorization);
}
