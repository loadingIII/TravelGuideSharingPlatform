package com.travel.service.impl;

import com.travel.mapper.AdminUserMapper;
import com.travel.pojo.model.AdminUser;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * 管理员认证服务实现类
 * 负责管理员登录、登出、会话管理、记住我功能等认证流程
 *
 * 核心流程：
 * 1. 登录：验证用户名密码 → 更新最后登录时间 → 设置会话
 * 2. 登出：清除会话 → 清除记住我Cookie
 * 3. 记住我：生成签名Cookie → 验证Cookie → 恢复会话
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private static final String SESSION_KEY = "adminUser";
    private static final String COOKIE_NAME = "admin_remember";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days

    @Value("${admin.remember-secret:TravelAdminSecret2024!}")
    private String hmacSecret;

    //TODO service需要写好注释,提高代码质量
    @Override
    public boolean login(String username, String password, HttpSession session) {
        AdminUser admin = adminUserMapper.selectByUsername(username);
        if (admin == null || admin.getStatus() != 1) {
            return false;
        }
        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            return false;
        }
        adminUserMapper.updateLastLoginAt(admin.getId());
        setSession(session, admin);
        return true;
    }

    @Override
    public void setSession(HttpSession session, AdminUser admin) {
        session.setAttribute(SESSION_KEY, Map.of(
                "id", admin.getId(),
                "username", admin.getUsername(),
                "realName", admin.getRealName() != null ? admin.getRealName() : admin.getUsername(),
                "role", admin.getRole() != null ? admin.getRole() : "EDITOR"
        ));
    }

    @Override
    public void logout(HttpSession session, HttpServletResponse response) {
        session.removeAttribute(SESSION_KEY);
        clearRememberCookie(response);
    }

    @Override
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_KEY) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCurrentAdmin(HttpSession session) {
        return (Map<String, Object>) session.getAttribute(SESSION_KEY);
    }

    @Override
    public void setRememberCookie(HttpServletResponse response, Long adminId) {
        long expiry = System.currentTimeMillis() + COOKIE_MAX_AGE * 1000L;
        String payload = adminId + "|" + expiry + "|" + UUID.randomUUID();
        String signature = sign(payload);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(
                (payload + "|" + signature).getBytes(StandardCharsets.UTF_8));
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    @Override
    public boolean tryRestoreSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String token = getCookieValue(request, COOKIE_NAME);
        if (token == null) return false;
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|");
            if (parts.length != 4) return false;
            long adminId = Long.parseLong(parts[0]);
            long expiry = Long.parseLong(parts[1]);
            String signature = parts[3];
            String payload = parts[0] + "|" + parts[1] + "|" + parts[2];
            if (!verify(payload, signature)) return false;
            if (System.currentTimeMillis() > expiry) return false;
            AdminUser admin = adminUserMapper.selectById(adminId);
            if (admin == null || admin.getStatus() != 1) return false;
            setSession(session, admin);
            // Refresh cookie if more than halfway expired
            long totalAge = COOKIE_MAX_AGE * 1000L;
            long remaining = expiry - System.currentTimeMillis();
            if (remaining < totalAge / 2) {
                setRememberCookie(response, adminId);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void clearRememberCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verify(String data, String expectedSignature) {
        return sign(data).equals(expectedSignature);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
