package com.travel.controller.admin;

import com.travel.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminIndexController {

    private final StatsService statsService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        var stats = statsService.stats();
        stats.forEach(model::addAttribute);
        return "admin/dashboard";
    }
}
