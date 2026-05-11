package com.travel.controller.admin;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.AdminUser;
import com.travel.service.AdminAdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/admins")
@RequiredArgsConstructor
public class AdminAdminUserController {

    private final AdminAdminUserService adminAdminUserService;

    @GetMapping
    public ApiResponse<PageResult<AdminUser>> list(@RequestParam(defaultValue = "1") int page) {
        return ApiResponse.success(adminAdminUserService.list(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminUser> detail(@PathVariable Long id) {
        AdminUser admin = adminAdminUserService.detail(id);
        if (admin == null) return ApiResponse.fail("NOT_FOUND", "管理员不存在");
        return ApiResponse.success(admin);
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            adminAdminUserService.create(body, session, request);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("VALIDATION_ERROR", e.getMessage());
        } catch (IllegalStateException e) {
            return ApiResponse.fail("BUSINESS_ERROR", e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            adminAdminUserService.update(id, body, session, request);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("NOT_FOUND", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            adminAdminUserService.delete(id, session, request);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("NOT_FOUND", e.getMessage());
        } catch (IllegalStateException e) {
            return ApiResponse.fail("BUSINESS_ERROR", e.getMessage());
        }
    }
}
