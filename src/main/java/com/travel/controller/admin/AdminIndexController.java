package com.travel.controller.admin;

import com.travel.service.AdminAuthService;
import com.travel.service.StatsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminIndexController {

    private final StatsService statsService;
    private final AdminAuthService adminAuthService;

    @GetMapping("/admin")
    public String adminRoot(HttpSession session) {
        if (adminAuthService.isLoggedIn(session)) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/login.html";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        var stats = statsService.stats();
        stats.forEach(model::addAttribute);
        return "admin/dashboard";
    }
}
