package com.travel.controller.admin;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.GuideComment;
import com.travel.service.AdminCommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentApiController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public ApiResponse<PageResult<GuideComment>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminCommentService.list(page, status));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminCommentService.delete(id, session, request);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        try {
            adminCommentService.audit(id, body, session, request);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("INVALID_ACTION", e.getMessage());
        }
    }
}
