package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.pojo.model.User;
import com.travel.service.AdminUserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserApiController {

    private final AdminUserManagementService adminUserManagementService;

    @GetMapping
    public ApiResponse<PageResult<User>> list(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminUserManagementService.list(page, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> result = adminUserManagementService.detail(id);
        if (result == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            adminUserManagementService.update(id, body, session, request);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("NOT_FOUND", e.getMessage());
        }
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        try {
            adminUserManagementService.audit(id, body.get("action"), session, request);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("BUSINESS_ERROR", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminUserManagementService.delete(id, session, request);
        return ApiResponse.success();
    }
}
