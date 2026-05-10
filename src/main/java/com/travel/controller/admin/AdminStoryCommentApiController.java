package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.StoryCommentMapper;
import com.travel.pojo.dto.ReviewResult;
import com.travel.pojo.model.StoryComment;
import com.travel.service.AuditLogService;
import com.travel.service.ReviewService;
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
    private final AuditLogService auditLogService;
    private final ReviewService reviewService;
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
        ReviewResult result = reviewService.resolveAction(action);
        if (result == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        storyCommentMapper.updateStatus(id, result.getStatus());
        auditLogService.log(session, request, "AUDIT", "STORY_COMMENT", id, result.getDetail());
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        storyCommentMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "STORY_COMMENT", id, "删除故事评论");
        return ApiResponse.success();
    }
}
