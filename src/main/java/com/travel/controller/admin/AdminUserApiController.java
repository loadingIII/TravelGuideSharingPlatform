package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.UserMapper;
import com.travel.mapper.UserProfileMapper;
import com.travel.pojo.model.User;
import com.travel.pojo.model.UserProfile;
import com.travel.pojo.dto.ReviewResult;
import com.travel.service.AdminAuthService;
import com.travel.service.AuditLogService;
import com.travel.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserApiController {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final AdminAuthService adminAuthService;
    private final AuditLogService auditLogService;
    private final ReviewService reviewService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<User>> list(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll(status);
        return ApiResponse.success(PageResult.of(userMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");
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
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");
        String username = (String) body.get("username");
        String phone = (String) body.get("phone");
        String email = (String) body.get("email");
        Integer status = body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : null;
        userMapper.updateUser(id, username, phone, email, status);
        auditLogService.log(session, request, "UPDATE", "USER", id, "编辑用户: " + username);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");

        ReviewResult result = reviewService.resolveAction(action, "disable", "enable");
        if (result == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        userMapper.updateStatus(id, result.getStatus());
        auditLogService.log(session, request, "AUDIT", "USER", id, result.getDetail() + ": " + user.getUsername());
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        User user = userMapper.selectById(id);
        userMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "USER", id, "删除用户: " + (user != null ? user.getUsername() : id));
        return ApiResponse.success();
    }
}
