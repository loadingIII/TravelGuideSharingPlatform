package com.travel.controller.admin;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.security.RequireRole;
import com.travel.service.AdminStoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 旅行故事管理控制器（管理员端）
 * 提供故事的 CRUD、审核等接口
 */
@RestController
@RequestMapping("/admin/stories")
@RequiredArgsConstructor
public class AdminStoryApiController {

    private final AdminStoryService adminStoryService;

    /**
     * 分页查询故事列表
     *
     * @param page   页码，默认1
     * @param status 审核状态筛选（可选）
     */
    @GetMapping
    public ApiResponse<PageResult<GuideStoryVO>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminStoryService.listStories(page, status));
    }

    /**
     * 获取故事详情
     *
     * @param id 故事ID
     */
    @GetMapping("/{id}")
    public ApiResponse<GuideStoryVO> detail(@PathVariable Long id) {
        GuideStoryVO story = adminStoryService.getStoryDetail(id);
        if (story == null) return ApiResponse.fail("NOT_FOUND", "故事不存在");
        return ApiResponse.success(story);
    }

    /**
     * 更新故事内容
     *
     * @param id      故事ID
     * @param body    更新的字段
     * @param session HTTP会话
     * @param request HTTP请求
     */
    @PutMapping("/{id}")
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
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

    /**
     * 新建故事
     *
     * @param body    故事数据
     * @param session HTTP会话
     * @param request HTTP请求
     */
    @PostMapping
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> create(@RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminStoryService.createStory(body, session, request);
        return ApiResponse.success();
    }

    /**
     * 审核故事
     *
     * @param id      故事ID
     * @param body    含action字段（approve/reject/down）
     * @param session HTTP会话
     * @param request HTTP请求
     */
    @PostMapping("/{id}/audit")
    @RequireRole({"ROOT", "ADMIN", "EDITOR", "VIEWER"})
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

    /**
     * 删除故事
     *
     * @param id      故事ID
     * @param session HTTP会话
     * @param request HTTP请求
     */
    @DeleteMapping("/{id}")
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminStoryService.deleteStory(id, session, request);
        return ApiResponse.success();
    }
}
