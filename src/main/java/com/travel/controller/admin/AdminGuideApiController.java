package com.travel.controller.admin;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideSummary;
import com.travel.security.RequireRole;
import com.travel.service.AdminGuideService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/guides")
@RequiredArgsConstructor
public class AdminGuideApiController {

    private final AdminGuideService adminGuideService;

    @GetMapping
    public ApiResponse<PageResult<GuideSummary>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminGuideService.listGuides(page, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<GuideSummary> detail(@PathVariable Long id) {
        GuideSummary guide = adminGuideService.getGuideDetail(id);
        if (guide == null) return ApiResponse.fail("NOT_FOUND", "攻略不存在");
        return ApiResponse.success(guide);
    }

    @GetMapping("/{id}/itinerary")
    public ApiResponse<List<GuideItineraryDay>> itinerary(@PathVariable Long id) {
        return ApiResponse.success(adminGuideService.getItinerary(id));
    }

    @GetMapping("/{id}/tags")
    public ApiResponse<?> guideTags(@PathVariable Long id) {
        return ApiResponse.success(adminGuideService.getGuideTags(id));
    }

    @PutMapping("/{id}")
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            adminGuideService.updateGuide(id, body, session, request);
            return ApiResponse.success();
        } catch (RuntimeException e) {
            return ApiResponse.fail("NOT_FOUND", e.getMessage());
        }
    }

    @PutMapping("/{id}/itinerary")
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> updateItinerary(@PathVariable Long id,
                                             @RequestBody Map<String, Object> body,
                                             HttpSession session,
                                             HttpServletRequest request) {
        try {
            adminGuideService.updateItinerary(id, body, session, request);
            return ApiResponse.success();
        } catch (RuntimeException e) {
            return ApiResponse.fail("NOT_FOUND", e.getMessage());
        }
    }

    @PostMapping
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> create(@RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminGuideService.createGuide(body, session, request);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/audit")
    @RequireRole({"ROOT", "ADMIN", "EDITOR", "VIEWER"})
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        try {
            adminGuideService.auditGuide(id, body.get("action"), session, request);
            return ApiResponse.success();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) return ApiResponse.fail("NOT_FOUND", e.getMessage());
            return ApiResponse.fail("INVALID_ACTION", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminGuideService.deleteGuide(id, session, request);
        return ApiResponse.success();
    }
}
