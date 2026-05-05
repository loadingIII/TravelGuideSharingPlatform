package com.travel.controller.admin;

import com.travel.mapper.AdminLogMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final AdminLogMapper adminLogMapper;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletRequest request,
                        Model model) {
        if (adminAuthService.login(username, password, session)) {
            Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
            AdminLog log = new AdminLog();
            log.setAdminId((Long) admin.get("id"));
            log.setAdminUsername((String) admin.get("username"));
            log.setAction("LOGIN");
            log.setTargetType("ADMIN");
            log.setDetail("管理员登录");
            log.setIpAddress(request.getRemoteAddr());
            adminLogMapper.insert(log);
            return "redirect:/admin";
        }
        model.addAttribute("error", "用户名或密码错误");
        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request) {
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
        return "redirect:/admin/login";
    }
}
