package com.travel.controller.user;

import com.travel.common.ApiResponse;

import com.travel.pojo.dto.AuthLoginDTO;
import com.travel.pojo.dto.AuthRegisterDTO;
import com.travel.pojo.vo.AuthTokenVO;
import com.travel.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理用户注册、登录、刷新令牌等认证相关操作
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 用户注册
     * 创建新用户账户并返回访问令牌
     *
     * @param request 注册请求，包含用户名、密码、手机号等信息
     * @return 访问令牌和刷新令牌
     */
    @PostMapping("/register")
    public ApiResponse<AuthTokenVO> register(@Valid @RequestBody AuthRegisterDTO request) {
        return ApiResponse.success(authService.register(request));
    }

    /**
     * 用户登录
     * 验证用户凭证并返回访问令牌
     *
     * @param request 登录请求，包含手机号和密码
     * @return 访问令牌和刷新令牌
     */
    @PostMapping("/login")
    public ApiResponse<AuthTokenVO> login(@Valid @RequestBody AuthLoginDTO request) {
        return ApiResponse.success(authService.login(request));
    }

    /**
     * 刷新访问令牌
     * 使用有效的刷新令牌获取新的访问令牌
     *
     * @param authorization 请求头中的授权信息（Bearer token）
     * @return 新的访问令牌和刷新令牌
     */
    @PostMapping("/refresh")
    public ApiResponse<AuthTokenVO> refresh(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.success(authService.refresh(authorization));
    }
}
