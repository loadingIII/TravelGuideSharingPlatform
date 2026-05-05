package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.GuideSummary;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/guides")
@RequiredArgsConstructor
public class AdminGuideApiController {

    private final GuideMapper guideMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<GuideSummary>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = guideMapper.countAll();
        return ApiResponse.success(PageResult.of(guideMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<GuideSummary> detail(@PathVariable Long id) {
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) return ApiResponse.fail("NOT_FOUND", "攻略不存在");
        return ApiResponse.success(guide);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        guideMapper.updateGuide(id, (String) body.get("title"), (String) body.get("summary"));
        logOperation(session, request, "UPDATE", "GUIDE", id, "修改攻略");
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        guideMapper.deleteById(id);
        logOperation(session, request, "DELETE", "GUIDE", id, "删除攻略");
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
