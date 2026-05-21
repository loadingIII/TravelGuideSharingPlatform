package com.travel.controller.admin;

import com.travel.service.AdminAuthService;
import com.travel.service.StatsService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AdminIndexController {

    private final AdminAuthService adminAuthService;
    private final StatsService statsService;

    @GetMapping("/")
    public void rootRedirect(HttpSession session, HttpServletResponse response) throws IOException {
        if (adminAuthService.isLoggedIn(session)) {
            response.sendRedirect("/admin/index.html");
        } else {
            response.sendRedirect("/admin/login.html");
        }
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        var stats = statsService.stats();
        stats.forEach(model::addAttribute);
        return "admin/dashboard";
    }
}
