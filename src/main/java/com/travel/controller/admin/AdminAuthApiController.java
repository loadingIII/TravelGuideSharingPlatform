package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.AdminUserMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.AdminUser;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthApiController {

    private final AdminAuthService adminAuthService;
    private final AdminLogMapper adminLogMapper;
    private final AdminUserMapper adminUserMapper;

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session,
                                   HttpServletRequest request) {
        log.info("登录尝试: username={}", username);
        AdminUser adminUser = adminUserMapper.selectByUsername(username);
        if (adminUser == null) {
            log.warn("用户不存在: {}", username);
            return ApiResponse.fail("AUTH_FAILED", "用户名或密码错误");
        }
        log.info("用户存在: id={}, status={}", adminUser.getId(), adminUser.getStatus());
        boolean ok = adminAuthService.login(username, password, session);
        if (!ok) {
            log.warn("登录失败: username={}", username);
            return ApiResponse.fail("AUTH_FAILED", "用户名或密码错误");
        }
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        AdminLog logEntry = new AdminLog();
        logEntry.setAdminId((Long) admin.get("id"));
        logEntry.setAdminUsername((String) admin.get("username"));
        logEntry.setAction("LOGIN");
        logEntry.setTargetType("ADMIN");
        logEntry.setDetail("管理员登录");
        logEntry.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(logEntry);
        log.info("登录成功: username={}", username);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session, HttpServletRequest request) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin != null) {
            AdminLog logEntry = new AdminLog();
            logEntry.setAdminId((Long) admin.get("id"));
            logEntry.setAdminUsername((String) admin.get("username"));
            logEntry.setAction("LOGOUT");
            logEntry.setTargetType("ADMIN");
            logEntry.setDetail("管理员登出");
            logEntry.setIpAddress(request.getRemoteAddr());
            adminLogMapper.insert(logEntry);
        }
        adminAuthService.logout(session);
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
