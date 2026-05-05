package com.travel.service;

import com.travel.mapper.AdminUserMapper;
import com.travel.pojo.model.AdminUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private static final String SESSION_KEY = "adminUser";

    public boolean login(String username, String password, HttpSession session) {
        AdminUser admin = adminUserMapper.selectByUsername(username);
        if (admin == null || admin.getStatus() != 1) {
            return false;
        }
        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            return false;
        }
        adminUserMapper.updateLastLoginAt(admin.getId());
        session.setAttribute(SESSION_KEY, Map.of(
                "id", admin.getId(),
                "username", admin.getUsername(),
                "realName", admin.getRealName() != null ? admin.getRealName() : admin.getUsername()
        ));
        return true;
    }

    public void logout(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_KEY) != null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCurrentAdmin(HttpSession session) {
        return (Map<String, Object>) session.getAttribute(SESSION_KEY);
    }
}
