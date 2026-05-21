package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.mapper.AdminUserMapper;
import com.travel.pojo.model.AdminUser;
import com.travel.service.AdminAdminUserService;
import com.travel.service.AdminAuthService;
import com.travel.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 管理员账户管理服务实现
 * 封装管理员 CRUD、密码加密、唯一性校验、自删保护等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AdminAdminUserServiceImpl implements AdminAdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final AdminAuthService adminAuthService;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;
    private static final int PAGE_SIZE = 10;

    private static final Map<String, Integer> ROLE_LEVEL = Map.of(
            "ROOT", 4, "ADMIN", 3, "EDITOR", 2, "VIEWER", 1
    );

    private void checkHierarchy(HttpSession session, String targetRole) {
        Map<String, Object> current = adminAuthService.getCurrentAdmin(session);
        if (current == null) return;
        String myRole = (String) current.get("role");
        int myLevel = ROLE_LEVEL.getOrDefault(myRole, 0);
        int targetLevel = ROLE_LEVEL.getOrDefault(targetRole, 0);
        if (myLevel < targetLevel) {
            throw new IllegalStateException("权限不足，无法操作更高级别的员工");
        }
    }

    @Override
    public PageResult<AdminUser> list(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = adminUserMapper.countAll();
        return PageResult.of(adminUserMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
    }

    @Override
    public AdminUser detail(Long id) {
        AdminUser admin = adminUserMapper.selectById(id);
        if (admin != null) {
            admin.setPasswordHash(null);
        }
        return admin;
    }

    @Override
    public void create(Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String realName = (String) body.get("realName");
        String role = (String) body.get("role");

        if (username == null || password == null) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        if (adminUserMapper.selectByUsername(username) != null) {
            throw new IllegalStateException("用户名已存在");
        }

        AdminUser admin = new AdminUser();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRealName(realName);
        admin.setRole(role != null ? role : "EDITOR");
        admin.setStatus(1);
        adminUserMapper.insert(admin);
        auditLogService.log(session, request, "CREATE", "ADMIN", admin.getId(), "新增员工: " + username + " 角色:" + admin.getRole());
    }

    @Override
    public void update(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        AdminUser existing = adminUserMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("管理员不存在");
        }
        checkHierarchy(session, existing.getRole());

        String realName = body.get("realName") != null ? (String) body.get("realName") : existing.getRealName();
        String role = body.get("role") != null ? (String) body.get("role") : existing.getRole();
        Integer status = body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : existing.getStatus();
        adminUserMapper.updateAdmin(id, realName, role, status);

        if (body.get("password") != null && !body.get("password").toString().isEmpty()) {
            adminUserMapper.updatePassword(id, passwordEncoder.encode((String) body.get("password")));
        }

        auditLogService.log(session, request, "UPDATE", "ADMIN", id, "编辑员工: " + existing.getUsername());
    }

    @Override
    public void delete(Long id, HttpSession session, HttpServletRequest request) {
        AdminUser existing = adminUserMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("管理员不存在");
        }
        checkHierarchy(session, existing.getRole());

        Map<String, Object> currentAdmin = adminAuthService.getCurrentAdmin(session);
        if (currentAdmin != null && currentAdmin.get("id").equals(id)) {
            throw new IllegalStateException("不能删除自己");
        }

        adminUserMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "ADMIN", id, "删除管理员: " + existing.getUsername());
    }
}
