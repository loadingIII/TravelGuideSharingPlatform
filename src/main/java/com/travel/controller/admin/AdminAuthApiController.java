package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.service.AdminAuthService;
import com.travel.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthApiController {

    private final AdminAuthService adminAuthService;
    private final AuditLogService auditLogService;

    @GetMapping
    public void rootRedirect(HttpSession session, HttpServletResponse response) throws IOException {
        if (adminAuthService.isLoggedIn(session)) {
            response.sendRedirect("/admin/index.html");
        } else {
            response.sendRedirect("/admin/login.html");
        }
    }

    @GetMapping("/login")
    public void loginRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/admin/login.html");
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam(required = false, defaultValue = "false") boolean remember,
                                   HttpSession session,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        log.info("登录尝试: username={}", username);
        boolean ok = adminAuthService.login(username, password, session);
        if (!ok) {
            log.warn("登录失败: username={}", username);
            return ApiResponse.fail("AUTH_FAILED", "用户名或密码错误");
        }
        if (remember) {
            Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
            if (admin != null) {
                adminAuthService.setRememberCookie(response, (Long) admin.get("id"));
            }
        }
        auditLogService.log(session, request, "LOGIN", "ADMIN", null, "管理员登录");
        log.info("登录成功: username={}", username);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        auditLogService.log(session, request, "LOGOUT", "ADMIN", null, "管理员登出");
        adminAuthService.logout(session, response);
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(HttpSession session) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) {
            return ApiResponse.fail("NOT_LOGIN", "未登录");
        }
        return ApiResponse.success(admin);
    }
}
