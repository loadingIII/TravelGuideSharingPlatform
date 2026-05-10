package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.GuideCommentMapper;
import com.travel.pojo.dto.ReviewResult;
import com.travel.pojo.model.GuideComment;
import com.travel.service.AuditLogService;
import com.travel.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentApiController {

    private final GuideCommentMapper commentMapper;
    private final AuditLogService auditLogService;
    private final ReviewService reviewService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<GuideComment>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = commentMapper.countAll(status);
        return ApiResponse.success(PageResult.of(commentMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        commentMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "COMMENT", id, "删除评论");
        return ApiResponse.success();
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        ReviewResult result = reviewService.resolveAction(action);
        if (result == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        commentMapper.updateStatus(id, result.getStatus());
        auditLogService.log(session, request, "AUDIT", "COMMENT", id, result.getDetail());
        return ApiResponse.success();
    }
}
