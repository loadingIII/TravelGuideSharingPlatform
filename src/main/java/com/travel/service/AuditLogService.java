package com.travel.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface AuditLogService {
    void log(HttpSession session, HttpServletRequest request,
             String action, String targetType, Long targetId, String detail);
}
