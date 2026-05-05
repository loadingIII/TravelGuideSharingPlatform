package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideCommentMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.GuideComment;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/comments")
@RequiredArgsConstructor
public class AdminCommentApiController {

    private final GuideCommentMapper commentMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<GuideComment>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = commentMapper.countAll();
        return ApiResponse.success(PageResult.of(commentMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        commentMapper.deleteById(id);
        logOperation(session, request, "DELETE", "COMMENT", id, "删除评论");
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
