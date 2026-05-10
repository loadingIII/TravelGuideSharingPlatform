package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminUserMapper;
import com.travel.pojo.model.AdminUser;
import com.travel.service.AdminAuthService;
import com.travel.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/admins")
@RequiredArgsConstructor
public class AdminAdminUserController {

    private final AdminUserMapper adminUserMapper;
    private final AdminAuthService adminAuthService;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<AdminUser>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = adminUserMapper.countAll();
        return ApiResponse.success(PageResult.of(adminUserMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminUser> detail(@PathVariable Long id) {
        AdminUser admin = adminUserMapper.selectById(id);
        if (admin == null) return ApiResponse.fail("NOT_FOUND", "管理员不存在");
        admin.setPasswordHash(null);
        return ApiResponse.success(admin);
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String realName = (String) body.get("realName");

        if (username == null || password == null) {
            return ApiResponse.fail("VALIDATION_ERROR", "用户名和密码不能为空");
        }

        if (adminUserMapper.selectByUsername(username) != null) {
            return ApiResponse.fail("BUSINESS_ERROR", "用户名已存在");
        }

        AdminUser admin = new AdminUser();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRealName(realName);
        admin.setStatus(1);
        adminUserMapper.insert(admin);

        auditLogService.log(session, request, "CREATE", "ADMIN", admin.getId(), "新增管理员: " + username);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        AdminUser existing = adminUserMapper.selectById(id);
        if (existing == null) return ApiResponse.fail("NOT_FOUND", "管理员不存在");

        String realName = body.get("realName") != null ? (String) body.get("realName") : existing.getRealName();
        Integer status = body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : existing.getStatus();
        adminUserMapper.updateAdmin(id, realName, status);

        if (body.get("password") != null && !body.get("password").toString().isEmpty()) {
            adminUserMapper.updatePassword(id, passwordEncoder.encode((String) body.get("password")));
        }

        auditLogService.log(session, request, "UPDATE", "ADMIN", id, "编辑管理员: " + existing.getUsername());
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        AdminUser existing = adminUserMapper.selectById(id);
        if (existing == null) return ApiResponse.fail("NOT_FOUND", "管理员不存在");

        Map<String, Object> currentAdmin = adminAuthService.getCurrentAdmin(session);
        if (currentAdmin != null && currentAdmin.get("id").equals(id)) {
            return ApiResponse.fail("BUSINESS_ERROR", "不能删除自己");
        }

        adminUserMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "ADMIN", id, "删除管理员: " + existing.getUsername());
        return ApiResponse.success();
    }
}
