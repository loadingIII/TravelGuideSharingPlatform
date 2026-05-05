package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.UserMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.User;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserApiController {

    private final UserMapper userMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<User>> list(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll(status);
        return ApiResponse.success(PageResult.of(userMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> detail(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");
        return ApiResponse.success(user);
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");

        Integer newStatus;
        String detail;
        switch (action) {
            case "approve" -> { newStatus = 1; detail = "审核通过"; }
            case "reject" -> { newStatus = 2; detail = "审核拒绝"; }
            case "disable" -> { newStatus = 2; detail = "禁用"; }
            case "enable" -> { newStatus = 1; detail = "启用"; }
            default -> { newStatus = null; detail = null; }
        }

        if (newStatus == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        userMapper.updateStatus(id, newStatus);
        logOperation(session, request, "AUDIT", "USER", id, detail + ": " + user.getUsername());
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        User user = userMapper.selectById(id);
        userMapper.deleteById(id);
        logOperation(session, request, "DELETE", "USER", id, "删除用户: " + (user != null ? user.getUsername() : id));
        return ApiResponse.success();
    }

    private void logOperation(HttpSession session, HttpServletRequest request,
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
