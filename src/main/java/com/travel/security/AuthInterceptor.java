package com.travel.security;

import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtTokenService jwtTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
        if (requireLogin == null) {
            requireLogin = handlerMethod.getBeanType().getAnnotation(RequireLogin.class);
        }
        if (requireLogin == null) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的登录凭证");
        }

        LoginUser loginUser = jwtTokenService.parseAccessToken(token);
        UserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
