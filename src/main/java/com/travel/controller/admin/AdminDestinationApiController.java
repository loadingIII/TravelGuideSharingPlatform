package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.Destination;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/destinations")
@RequiredArgsConstructor
public class AdminDestinationApiController {

    private final DestinationMapper destinationMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<Destination>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = destinationMapper.countAll();
        return ApiResponse.success(PageResult.of(destinationMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<Destination> detail(@PathVariable Long id) {
        Destination dest = destinationMapper.selectById(id);
        if (dest == null) return ApiResponse.fail("NOT_FOUND", "目的地不存在");
        return ApiResponse.success(dest);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        destinationMapper.updateDestination(id,
            (String) body.get("name"), (String) body.get("country"),
            (String) body.get("city"), (String) body.get("description"),
            (String) body.get("coverImageUrl"));
        logOperation(session, request, "UPDATE", "DESTINATION", id, "修改目的地");
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        destinationMapper.deleteById(id);
        logOperation(session, request, "DELETE", "DESTINATION", id, "删除目的地");
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
