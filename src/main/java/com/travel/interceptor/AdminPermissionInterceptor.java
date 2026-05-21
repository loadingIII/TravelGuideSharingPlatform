package com.travel.interceptor;

import com.travel.security.RequireRole;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminPermissionInterceptor implements HandlerInterceptor {

    private final AdminAuthService adminAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole annotation = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (annotation == null) {
            return true;
        }

        HttpSession session = request.getSession();
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"NOT_LOGIN\",\"message\":\"未登录\",\"data\":null}");
            return false;
        }

        String userRole = (String) admin.get("role");
        String[] allowedRoles = annotation.value();

        boolean hasPermission = Arrays.stream(allowedRoles)
                .anyMatch(r -> r.equals(userRole));

        if (!hasPermission) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"FORBIDDEN\",\"message\":\"权限不足\",\"data\":null}");
            return false;
        }

        return true;
    }
}
