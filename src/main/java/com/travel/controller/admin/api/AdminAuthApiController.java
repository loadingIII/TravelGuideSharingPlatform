package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.mapper.AdminLogMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminAuthApiController {

    private final AdminAuthService adminAuthService;
    private final AdminLogMapper adminLogMapper;

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session,
                                   HttpServletRequest request) {
        if (!adminAuthService.login(username, password, session)) {
            return ApiResponse.fail("AUTH_FAILED", "用户名或密码错误");
        }
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        AdminLog log = new AdminLog();
        log.setAdminId((Long) admin.get("id"));
        log.setAdminUsername((String) admin.get("username"));
        log.setAction("LOGIN");
        log.setTargetType("ADMIN");
        log.setDetail("管理员登录");
        log.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(log);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session, HttpServletRequest request) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin != null) {
            AdminLog log = new AdminLog();
            log.setAdminId((Long) admin.get("id"));
            log.setAdminUsername((String) admin.get("username"));
            log.setAction("LOGOUT");
            log.setTargetType("ADMIN");
            log.setDetail("管理员登出");
            log.setIpAddress(request.getRemoteAddr());
            adminLogMapper.insert(log);
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
