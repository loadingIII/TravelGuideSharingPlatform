package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.mapper.DestinationMapper;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.model.Destination;
import com.travel.service.AdminDestinationService;
import com.travel.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 目的地管理服务实现
 * 封装目的地的 CRUD、外键约束检查（有关联攻略时禁止删除）等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AdminDestinationServiceImpl implements AdminDestinationService {

    private final DestinationMapper destinationMapper;
    private final GuideMapper guideMapper;
    private final AuditLogService auditLogService;
    private static final int PAGE_SIZE = 10;

    @Override
    public PageResult<Destination> list(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = destinationMapper.countAll();
        return PageResult.of(destinationMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
    }

    @Override
    public Destination detail(Long id) {
        return destinationMapper.selectById(id);
    }

    @Override
    public void create(Destination destination, HttpSession session, HttpServletRequest request) {
        destinationMapper.insert(destination);
        auditLogService.log(session, request, "CREATE", "DESTINATION", destination.getId(), "新增目的地: " + destination.getName());
    }

    @Override
    public void update(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        destinationMapper.updateDestination(id,
            (String) body.get("name"), (String) body.get("country"),
            (String) body.get("city"), (String) body.get("description"),
            (String) body.get("coverImageUrl"));
        auditLogService.log(session, request, "UPDATE", "DESTINATION", id, "修改目的地");
    }

    @Override
    public void delete(Long id, HttpSession session, HttpServletRequest request) {
        long guideCount = guideMapper.countGuidesByDestination(id);
        if (guideCount > 0) {
            throw new IllegalStateException("该目的地下有 " + guideCount + " 条攻略，请先删除攻略后再删除目的地");
        }
        destinationMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "DESTINATION", id, "删除目的地");
    }
}
