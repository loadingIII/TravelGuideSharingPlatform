package com.travel.controller.admin;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.StoryComment;
import com.travel.service.AdminStoryCommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/story-comments")
@RequiredArgsConstructor
public class AdminStoryCommentApiController {

    private final AdminStoryCommentService adminStoryCommentService;

    @GetMapping
    public ApiResponse<PageResult<StoryComment>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminStoryCommentService.list(page, status));
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        try {
            adminStoryCommentService.audit(id, body, session, request);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("INVALID_ACTION", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminStoryCommentService.delete(id, session, request);
        return ApiResponse.success();
    }
}
