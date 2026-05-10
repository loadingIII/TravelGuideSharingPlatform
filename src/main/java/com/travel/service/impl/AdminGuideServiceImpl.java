package com.travel.service.impl;

import com.travel.common.PageResult;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.dto.ReviewResult;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideSummary;
import com.travel.service.AdminAuthService;
import com.travel.service.AdminGuideService;
import com.travel.service.AuditLogService;
import com.travel.service.ReviewService;
import com.travel.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminGuideServiceImpl implements AdminGuideService {

    private final GuideMapper guideMapper;
    private final AdminAuthService adminAuthService;
    private final AuditLogService auditLogService;
    private final ReviewService reviewService;
    private final TagService tagService;
    private static final int PAGE_SIZE = 10;

    @Override
    public PageResult<GuideSummary> listGuides(int page, Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = guideMapper.countAll(status);
        return PageResult.of(guideMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total);
    }

    @Override
    public GuideSummary getGuideDetail(Long id) {
        return guideMapper.selectById(id);
    }

    @Override
    public List<GuideItineraryDay> getItinerary(Long id) {
        return guideMapper.listItineraryDays(id);
    }

    @Override
    public Map<String, Object> getTips(Long id) {
        return Map.of("categories", List.of(), "items", List.of());
    }

    @Override
    public Object getGuideTags(Long id) {
        return tagService.getTagsByGuideId(id);
    }

    @Override
    public void createGuide(Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        GuideSummary guide = new GuideSummary();
        guide.setDestinationId(body.get("destinationId") != null ? Long.valueOf(body.get("destinationId").toString()) : null);
        guide.setAuthorId(admin != null ? (Long) admin.get("id") : null);
        guide.setTitle((String) body.get("title"));
        guide.setSummary((String) body.get("summary"));
        guide.setContentHtml((String) body.get("contentHtml"));
        guide.setCoverImageUrl((String) body.get("coverImageUrl"));
        guide.setStatus(0);
        guideMapper.insert(guide);
        auditLogService.log(session, request, "CREATE", "GUIDE", guide.getId(), "新增攻略: " + guide.getTitle());
    }

    @Override
    public void updateGuide(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) throw new RuntimeException("攻略不存在");

        String title = body.get("title") != null ? (String) body.get("title") : guide.getTitle();
        String summary = body.get("summary") != null ? (String) body.get("summary") : guide.getSummary();
        String contentHtml = body.get("contentHtml") != null ? (String) body.get("contentHtml") : guide.getContentHtml();
        String coverImageUrl = body.get("coverImageUrl") != null ? (String) body.get("coverImageUrl") : guide.getCoverImageUrl();
        String locationText = body.get("locationText") != null ? (String) body.get("locationText") : guide.getLocationText();
        String scope = body.get("scope") != null ? (String) body.get("scope") : guide.getScope();
        String travelMode = body.get("travelMode") != null ? (String) body.get("travelMode") : guide.getTravelMode();

        guideMapper.updateGuideMetadata(id, title, summary, contentHtml, coverImageUrl, locationText, scope, travelMode);
        auditLogService.log(session, request, "UPDATE", "GUIDE", id, "编辑攻略: " + title);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void updateItinerary(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) throw new RuntimeException("攻略不存在");

        guideMapper.deleteItineraryDaysByGuideId(id);

        List<Map<String, Object>> days = (List<Map<String, Object>>) body.get("days");
        if (days != null) {
            int daySort = 1;
            for (Map<String, Object> dayData : days) {
                GuideItineraryDay day = new GuideItineraryDay();
                day.setGuideId(id);
                day.setDayNo(dayData.get("dayNo") != null ? Integer.valueOf(dayData.get("dayNo").toString()) : daySort);
                day.setTitle((String) dayData.get("title"));
                day.setSummary((String) dayData.get("summary"));
                day.setSortOrder(daySort++);
                guideMapper.insertItineraryDay(day);
            }
        }
        auditLogService.log(session, request, "UPDATE", "GUIDE_ITINERARY", id, "更新行程安排");
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void updateTips(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) throw new RuntimeException("攻略不存在");
        auditLogService.log(session, request, "UPDATE", "GUIDE_TIPS", id, "更新攻略贴士");
    }

    @Override
    public void auditGuide(Long id, String action, HttpSession session, HttpServletRequest request) {
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) throw new RuntimeException("攻略不存在");

        ReviewResult result = reviewService.resolveAction(action);
        if (result == null) throw new RuntimeException("无效的操作");

        guideMapper.updateStatus(id, result.getStatus());
        auditLogService.log(session, request, "AUDIT", "GUIDE", id, result.getDetail() + ": " + guide.getTitle());
    }

    @Override
    @Transactional
    public void deleteGuide(Long id, HttpSession session, HttpServletRequest request) {
        guideMapper.deleteItineraryDaysByGuideId(id);
        tagService.syncGuideTags(id, null);
        guideMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "GUIDE", id, "删除攻略");
    }
}
