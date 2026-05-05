package com.travel.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String SESSION_KEY = "adminUser";

    public boolean login(String username, String password, HttpSession session) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute(SESSION_KEY, Map.of("username", username));
            return true;
        }
        return false;
    }

    public void logout(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_KEY) != null;
    }
}
