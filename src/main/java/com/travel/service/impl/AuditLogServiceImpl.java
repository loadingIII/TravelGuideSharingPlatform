package com.travel.service.impl;

import com.travel.mapper.AdminLogMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.service.AdminAuthService;
import com.travel.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

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
            log.setAdminId((Long) admin.get("id"));
            log.setAdminUsername((String) admin.get("username"));
            log.setAction(action);
            log.setTargetType(targetType);
            log.setTargetId(targetId);
            log.setDetail(detail);
            log.setIpAddress(request.getRemoteAddr());
            adminLogMapper.insert(log);
        } catch (Exception e) {
            // 日志记录失败不影响业务操作
        }
    }
}
