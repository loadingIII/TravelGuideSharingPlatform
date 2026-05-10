package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class AdminStatsApiController {

    private final StatsService statsService;

    @GetMapping
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.success(statsService.stats());
    }
}
