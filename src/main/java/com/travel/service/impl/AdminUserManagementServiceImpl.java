package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.mapper.UserMapper;
import com.travel.mapper.UserProfileMapper;
import com.travel.pojo.dto.ReviewResult;
import com.travel.pojo.model.User;
import com.travel.pojo.model.UserProfile;
import com.travel.service.AdminUserManagementService;
import com.travel.service.AuditLogService;
import com.travel.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 普通用户管理服务实现
 * 封装前台注册用户的 CRUD、审核、状态管理、DTO组装等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AdminUserManagementServiceImpl implements AdminUserManagementService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final AuditLogService auditLogService;
    private final ReviewService reviewService;
    private static final int PAGE_SIZE = 10;

    @Override
    public PageResult<User> list(int page, Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll(status);
        return PageResult.of(userMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total);
    }

    @Override
    public Map<String, Object> detail(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) return null;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("status", user.getStatus());
        result.put("lastLoginAt", user.getLastLoginAt());
        result.put("createdAt", user.getCreatedAt());
        result.put("updatedAt", user.getUpdatedAt());

        UserProfile profile = userProfileMapper.selectByUserId(id);
        if (profile != null) {
            result.put("nickname", profile.getNickname());
            result.put("avatarUrl", profile.getAvatarUrl());
            result.put("bio", profile.getBio());
            result.put("isVip", profile.getIsVip());
        }
        return result;
    }

    @Override
    public void update(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        String username = (String) body.get("username");
        String phone = (String) body.get("phone");
        String email = (String) body.get("email");
        Integer status = body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : null;
        userMapper.updateUser(id, username, phone, email, status);
        auditLogService.log(session, request, "UPDATE", "USER", id, "编辑用户: " + username);
    }

    @Override
    public void audit(Long id, String action, HttpSession session, HttpServletRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        ReviewResult result = reviewService.resolveAction(action, "disable", "enable");
        if (result == null) {
            throw new IllegalArgumentException("无效的操作");
        }

        userMapper.updateStatus(id, result.getStatus());
        auditLogService.log(session, request, "AUDIT", "USER", id, result.getDetail() + ": " + user.getUsername());
    }

    @Override
    public void delete(Long id, HttpSession session, HttpServletRequest request) {
        User user = userMapper.selectById(id);
        userMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "USER", id, "删除用户: " + (user != null ? user.getUsername() : id));
    }
}
