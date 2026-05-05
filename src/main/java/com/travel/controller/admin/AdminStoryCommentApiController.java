package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.StoryCommentMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.StoryComment;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/story-comments")
@RequiredArgsConstructor
public class AdminStoryCommentApiController {

    private final StoryCommentMapper storyCommentMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<StoryComment>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyCommentMapper.countAll(status);
        return ApiResponse.success(PageResult.of(storyCommentMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        Integer newStatus;
        String detail;
        switch (action) {
            case "approve" -> { newStatus = 1; detail = "审核通过"; }
            case "reject" -> { newStatus = 2; detail = "审核拒绝"; }
            case "down" -> { newStatus = 3; detail = "下架"; }
            default -> { newStatus = null; detail = null; }
        }
        if (newStatus == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");
        storyCommentMapper.updateStatus(id, newStatus);
        logOperation(session, request, "AUDIT", "STORY_COMMENT", id, detail);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        storyCommentMapper.deleteById(id);
        logOperation(session, request, "DELETE", "STORY_COMMENT", id, "删除故事评论");
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
