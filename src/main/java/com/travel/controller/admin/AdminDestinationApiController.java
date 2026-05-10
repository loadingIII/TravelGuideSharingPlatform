package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.DestinationMapper;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.model.Destination;
import com.travel.service.AuditLogService;
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
    private final GuideMapper guideMapper;
    private final AuditLogService auditLogService;
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

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Destination destination,
                                    HttpSession session,
                                    HttpServletRequest request) {
        destinationMapper.insert(destination);
        auditLogService.log(session, request, "CREATE", "DESTINATION", destination.getId(), "新增目的地: " + destination.getName());
        return ApiResponse.success();
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
        auditLogService.log(session, request, "UPDATE", "DESTINATION", id, "修改目的地");
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        long guideCount = guideMapper.countGuidesByDestination(id);
        if (guideCount > 0) {
            return ApiResponse.fail("HAS_GUIDES", "该目的地下有 " + guideCount + " 条攻略，请先删除攻略后再删除目的地");
        }
        destinationMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "DESTINATION", id, "删除目的地");
        return ApiResponse.success();
    }
}
