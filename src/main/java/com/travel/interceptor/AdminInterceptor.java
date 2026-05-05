package com.travel.interceptor;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!adminAuthService.isLoggedIn(request.getSession())) {
            String accept = request.getHeader("Accept");
            boolean isAjax = accept != null && accept.contains("application/json");
            String uri = request.getRequestURI();
            boolean isStatic = uri.contains(".html") || uri.contains(".css") || uri.contains(".js") || uri.contains(".png") || uri.contains(".jpg");
            if (isAjax || (!isStatic && !uri.equals("/admin/login"))) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":\"NOT_LOGIN\",\"message\":\"未登录\",\"data\":null}");
                return false;
            }
            response.sendRedirect("/admin/login.html");
            return false;
        }
        return true;
    }
}
