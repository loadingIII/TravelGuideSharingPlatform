package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.service.AdminStoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/stories")
@RequiredArgsConstructor
public class AdminStoryApiController {

    private final AdminStoryService adminStoryService;

    @GetMapping
    public ApiResponse<PageResult<GuideStoryVO>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminStoryService.listStories(page, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<GuideStoryVO> detail(@PathVariable Long id) {
        GuideStoryVO story = adminStoryService.getStoryDetail(id);
        if (story == null) return ApiResponse.fail("NOT_FOUND", "故事不存在");
        return ApiResponse.success(story);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            adminStoryService.updateStory(id, body, session, request);
            return ApiResponse.success();
        } catch (RuntimeException e) {
            return ApiResponse.fail("NOT_FOUND", e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminStoryService.createStory(body, session, request);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        try {
            adminStoryService.auditStory(id, body.get("action"), session, request);
            return ApiResponse.success();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) return ApiResponse.fail("NOT_FOUND", e.getMessage());
            return ApiResponse.fail("INVALID_ACTION", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminStoryService.deleteStory(id, session, request);
        return ApiResponse.success();
    }
}
