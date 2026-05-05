package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideStoryMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/stories")
@RequiredArgsConstructor
public class AdminStoryApiController {

    private final GuideStoryMapper storyMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<GuideStoryVO>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyMapper.countAll();
        return ApiResponse.success(PageResult.of(storyMapper.selectAll(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<GuideStoryVO> detail(@PathVariable Long id) {
        GuideStoryVO story = storyMapper.selectById(id);
        if (story == null) return ApiResponse.fail("NOT_FOUND", "故事不存在");
        return ApiResponse.success(story);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        storyMapper.updateContent(id, (String) body.get("content"));
        logOperation(session, request, "UPDATE", "STORY", id, "修改故事");
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        storyMapper.deleteById(id);
        logOperation(session, request, "DELETE", "STORY", id, "删除故事");
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
