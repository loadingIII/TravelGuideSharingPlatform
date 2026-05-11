package com.travel.service.impl;

import com.travel.mapper.AdminLogMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.service.AdminAuthService;
import com.travel.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;

    @Override
    public void log(HttpSession session, HttpServletRequest request,
                    String action, String targetType, Long targetId, String detail) {
        try {
            Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
            if (admin == null) return;
            AdminLog log = new AdminLog();
            log.setAdminId(((Number) admin.get("id")).longValue());
            log.setAdminUsername((String) admin.get("username"));
            log.setAction(action);
            log.setTargetType(targetType);
            log.setTargetId(targetId);
            log.setDetail(detail);
            log.setIpAddress(request.getRemoteAddr());
            adminLogMapper.insert(log);
        } catch (Exception e) {
            log.warn("审计日志记录失败: action={}, targetType={}, targetId={}", action, targetType, targetId, e);
        }
    }
}
