package com.travel.service.impl;

import com.travel.common.PageResult;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.dto.ReviewResult;
import com.travel.pojo.model.GuideBudgetItem;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideItinerarySpot;
import com.travel.pojo.model.GuideSummary;
import com.travel.pojo.model.GuideTipCategory;
import com.travel.pojo.model.GuideTipItem;
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
    public Map<String, Object> getItinerary(Long id) {
        List<GuideItineraryDay> days = guideMapper.listItineraryDays(id);
        List<Long> dayIds = days.stream().map(GuideItineraryDay::getId).toList();
        List<GuideItinerarySpot> spots = dayIds.isEmpty() ? List.of() : guideMapper.listItinerarySpotsByDayIds(dayIds);
        return Map.of("days", days, "spots", spots);
    }

    @Override
    public Map<String, Object> getTips(Long id) {
        List<GuideTipCategory> categories = guideMapper.listTipCategories(id);
        List<Long> tipIds = categories.stream().map(GuideTipCategory::getId).toList();
        List<GuideTipItem> items = tipIds.isEmpty() ? List.of() : guideMapper.listTipItemsByTipIds(tipIds);
        return Map.of("categories", categories, "items", items);
    }

    @Override
    public List<GuideBudgetItem> getBudget(Long id) {
        return guideMapper.listBudgetItems(id);
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

                List<Map<String, Object>> spots = (List<Map<String, Object>>) dayData.get("spots");
                if (spots != null) {
                    int spotSort = 1;
                    for (Map<String, Object> spotData : spots) {
                        GuideItinerarySpot spot = new GuideItinerarySpot();
                        spot.setItineraryDayId(day.getId());
                        spot.setName((String) spotData.get("name"));
                        spot.setDescription((String) spotData.get("description"));
                        spot.setSortOrder(spotSort++);
                        guideMapper.insertItinerarySpot(spot);
                    }
                }
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

        guideMapper.deleteTipsByGuideId(id);

        List<Map<String, Object>> categories = (List<Map<String, Object>>) body.get("categories");
        if (categories != null) {
            int tipSort = 1;
            for (Map<String, Object> catData : categories) {
                GuideTipCategory tip = new GuideTipCategory();
                tip.setGuideId(id);
                tip.setCategoryName((String) catData.get("categoryName"));
                tip.setSortOrder(tipSort++);
                guideMapper.insertTipCategory(tip);

                List<String> items = (List<String>) catData.get("items");
                if (items != null) {
                    int itemSort = 1;
                    for (String itemText : items) {
                        GuideTipItem item = new GuideTipItem();
                        item.setTipId(tip.getId());
                        item.setItemText(itemText);
                        item.setSortOrder(itemSort++);
                        guideMapper.insertTipItem(item);
                    }
                }
            }
        }
        auditLogService.log(session, request, "UPDATE", "GUIDE_TIPS", id, "更新攻略贴士");
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void updateBudget(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) throw new RuntimeException("攻略不存在");

        guideMapper.deleteBudgetItemsByGuideId(id);

        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        if (items != null) {
            int sort = 1;
            for (Map<String, Object> itemData : items) {
                GuideBudgetItem item = new GuideBudgetItem();
                item.setGuideId(id);
                item.setCategoryCode((String) itemData.get("categoryCode"));
                item.setCategoryName((String) itemData.get("categoryName"));
                item.setSortOrder(sort++);
                guideMapper.insertBudgetItem(item);
            }
        }
        auditLogService.log(session, request, "UPDATE", "GUIDE_BUDGET", id, "更新预算明细");
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
        guideMapper.deleteTipsByGuideId(id);
        guideMapper.deleteBudgetItemsByGuideId(id);
        tagService.syncGuideTags(id, null);
        guideMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "GUIDE", id, "删除攻略");
    }
}
