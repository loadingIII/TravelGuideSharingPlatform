package com.travel.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.common.ApiResponse;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final AdminAuthService adminAuthService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!adminAuthService.isLoggedIn(request.getSession())) {
            String uri = request.getRequestURI();
            if (uri.startsWith("/admin/api/")) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResponse.fail("NOT_LOGIN", "未登录")));
                return false;
            }
            response.sendRedirect("/admin/login.html");
            return false;
        }
        return true;
    }
}
