package com.travel.controller.admin;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.model.Destination;
import com.travel.security.RequireRole;
import com.travel.service.AdminDestinationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/destinations")
@RequiredArgsConstructor
public class AdminDestinationApiController {

    private final AdminDestinationService adminDestinationService;

    @GetMapping
    public ApiResponse<PageResult<Destination>> list(@RequestParam(defaultValue = "1") int page) {
        return ApiResponse.success(adminDestinationService.list(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<Destination> detail(@PathVariable Long id) {
        Destination dest = adminDestinationService.detail(id);
        if (dest == null) return ApiResponse.fail("NOT_FOUND", "目的地不存在");
        return ApiResponse.success(dest);
    }

    @PostMapping
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> create(@RequestBody Destination destination,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminDestinationService.create(destination, session, request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        adminDestinationService.update(id, body, session, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ROOT", "ADMIN", "EDITOR"})
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            adminDestinationService.delete(id, session, request);
            return ApiResponse.success();
        } catch (IllegalStateException e) {
            return ApiResponse.fail("HAS_GUIDES", e.getMessage());
        }
    }
}
