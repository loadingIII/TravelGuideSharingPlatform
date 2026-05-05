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
    public ApiResponse<PageResult<User>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll();
        return ApiResponse.success(PageResult.of(userMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> detail(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");
        return ApiResponse.success(user);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        userMapper.updateUser(id,
            (String) body.get("username"),
            (String) body.get("phone"),
            (String) body.get("email"),
            (Integer) body.get("status"));
        logOperation(session, request, "UPDATE", "USER", id, "修改用户: " + body.get("username"));
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
    }
}
