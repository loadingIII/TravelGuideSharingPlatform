package com.travel.controller.admin;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.AdminLog;
import com.travel.security.RequireRole;
import com.travel.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
@RequireRole({"ROOT", "ADMIN"})
public class AdminLogController {

    private final AdminLogService adminLogService;

    @GetMapping
    public ApiResponse<PageResult<AdminLog>> list(@RequestParam(defaultValue = "1") int page) {
        return ApiResponse.success(adminLogService.list(page));
    }
}
