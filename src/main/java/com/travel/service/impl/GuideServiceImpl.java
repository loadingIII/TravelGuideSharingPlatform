package com.travel.service.impl;

import com.travel.common.PageResult;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ErrorCode;

import com.travel.pojo.model.GuideBudgetItem;
import com.travel.pojo.model.GuideDetail;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideItinerarySpot;
import com.travel.pojo.model.GuideRelated;
import com.travel.pojo.model.GuideSummary;
import com.travel.pojo.model.GuideTipCategory;
import com.travel.pojo.model.GuideTipItem;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.vo.GuideDetailVO;
import com.travel.pojo.vo.GuideListItemVO;
import com.travel.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 攻略服务实现类
 * 负责旅游攻略的查询、搜索和详情组装
 *
 * 核心功能：
 * - 分页查询攻略列表（支持多条件筛选）
 * - 组装攻略详情页数据（行程、攻略、预算、相关推荐等多表关联）
 * - 社区精选、热门攻略等推荐功能
 *
 * 数据模型关系（攻略详情涉及的多张表）：
 * - GuideSummary: 攻略摘要信息（标题、封面、统计数据等）
 * - GuideDetail: 攻略完整内容（正文 HTML、详细描述等）
 * - GuideItineraryDay: 行程天数（如"Day 1: 到达东京"）
 * - GuideItinerarySpot: 行程景点（每天的具体景点安排）
 * - GuideTipCategory: 攻略分类（如"交通"、"住宿"、"美食"）
 * - GuideTipItem: 攻略条目（每个分类下的具体建议）
 * - GuideBudgetItem: 预算明细（各项费用的金额和占比）
 * - GuideRelated: 相关攻略推荐
 */
@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {
    private final GuideMapper guideMapper;

    /**
     * 分页获取攻略列表
     * 支持按关键词、旅行范围（国内/国际）、旅行方式（自由行/跟团等）、排序方式筛选
     *
     * @param keyword    搜索关键词，匹配攻略标题和内容
     * @param scope      旅行范围：domestic（国内）、international（国际）
     * @param travelMode 旅行方式：free（自由行）、group（跟团）、family（亲子）、honeymoon（蜜月）
     * @param sort       排序方式：latest（最新）、popular（最热）等
     */
    @Override
    public PageResult<GuideListItemVO> listGuides(String keyword, String scope, String travelMode, String sort, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        List<GuideListItemVO> list = guideMapper.listGuides(keyword, scope, travelMode, sort, offset, safePageSize)
                .stream()
                .map(this::toGuideListItem)
                .toList();
        long total = guideMapper.countGuides(keyword, scope, travelMode);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    /**
     * 获取指定目的地下的攻略列表
     * 用于目的地详情页展示该目的地的所有攻略
     */
    @Override
    public PageResult<GuideListItemVO> listGuidesByDestination(Long destinationId, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        List<GuideListItemVO> list = guideMapper.listGuidesByDestination(destinationId, offset, safePageSize)
                .stream()
                .map(this::toGuideListItem)
                .toList();
        long total = guideMapper.countGuidesByDestination(destinationId);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    /**
     * 获取攻略详情页完整数据
     * 这是最复杂的方法，需要从多张表查询数据并组装成嵌套结构
     *
     * 数据组装流程：
     * 1. 查询攻略基础信息（GuideDetail）
     * 2. 查询行程天数（GuideItineraryDay）→ 再查每天的景点（GuideItinerarySpot）→ 按天分组
     * 3. 查询攻略分类（GuideTipCategory）→ 再查每个分类下的建议（GuideTipItem）→ 按分类分组
     * 4. 查询预算明细（GuideBudgetItem）
     * 5. 查询相关攻略推荐（GuideRelated）
     * 6. 将所有数据组装成 GuideDetailVO 返回
     */
    @Override
    public GuideDetailVO getGuideDetail(Long guideId) {
        // 1. 查询攻略主体信息
        GuideDetail detail = guideMapper.selectGuideDetail(guideId);
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "攻略不存在");
        }

        // 2. 查询行程安排：先查天数，再查每天的景点，按天分组
        List<GuideItineraryDay> itineraryDays = guideMapper.listItineraryDays(guideId);
        List<GuideItinerarySpot> spots = itineraryDays.isEmpty()
                ? Collections.emptyList()
                : guideMapper.listItinerarySpotsByDayIds(itineraryDays.stream().map(GuideItineraryDay::getId).toList());
        // 将景点按所属天数分组，便于后续组装嵌套结构
        Map<Long, List<GuideItinerarySpot>> spotsGroup = spots.stream()
                .collect(Collectors.groupingBy(GuideItinerarySpot::getItineraryDayId));

        // 3. 查询攻略建议：先查分类，再查每个分类下的具体建议，按分类分组
        List<GuideTipCategory> tipCategories = guideMapper.listTipCategories(guideId);
        List<GuideTipItem> tipItems = tipCategories.isEmpty()
                ? Collections.emptyList()
                : guideMapper.listTipItemsByTipIds(tipCategories.stream().map(GuideTipCategory::getId).toList());
        Map<Long, List<GuideTipItem>> tipItemGroup = tipItems.stream()
                .collect(Collectors.groupingBy(GuideTipItem::getTipId));

        // 4. 查询预算明细和相关攻略
        List<GuideBudgetItem> budgetItems = guideMapper.listBudgetItems(guideId);
        List<GuideRelated> relatedGuides = guideMapper.listRelatedGuides(guideId, 4);

        // 5. 组装完整的详情 VO 返回
        return GuideDetailVO.builder()
                .id(detail.getId())
                .destinationId(detail.getDestinationId())
                .destinationName(detail.getDestinationName())
                .authorId(detail.getAuthorId())
                .authorName(detail.getAuthorName())
                .authorAvatarUrl(detail.getAuthorAvatarUrl())
                .title(detail.getTitle())
                .summary(detail.getSummary())
                .contentHtml(detail.getContentHtml())
                .coverImageUrl(detail.getCoverImageUrl())
                .locationText(detail.getLocationText())
                .scope(detail.getScope())
                .travelMode(detail.getTravelMode())
                .days(detail.getDays())
                .budgetTotal(detail.getBudgetTotal())
                .viewsCount(detail.getViewsCount())
                .likesCount(detail.getLikesCount())
                .commentsCount(detail.getCommentsCount())
                .favoritesCount(detail.getFavoritesCount())
                .publishedAt(detail.getPublishedAt())
                .itinerary(itineraryDays.stream().map(day -> toItineraryDay(day, spotsGroup)).toList())
                .tips(tipCategories.stream().map(tip -> toTipCategory(tip, tipItemGroup)).toList())
                .budgetItems(budgetItems.stream().map(this::toBudgetItem).toList())
                .relatedGuides(relatedGuides.stream().map(this::toRelatedGuide).toList())
                .build();
    }

    /**
     * 获取社区精选攻略
     * 由运营或算法选出的优质攻略，用于首页展示
     */
    @Override
    public List<GuideListItemVO> listCommunityPicks(int limit) {
        return guideMapper.listCommunityPicks(limit)
                .stream()
                .map(this::toGuideListItem)
                .toList();
    }

    /**
     * 搜索攻略
     * 根据关键词模糊匹配攻略标题和内容
     */
    @Override
    public List<GuideListItemVO> searchGuides(String query, int limit) {
        return guideMapper.searchGuides(query, limit)
                .stream()
                .map(this::toGuideListItem)
                .toList();
    }

    /**
     * 获取点赞数最多的热门攻略
     */
    @Override
    public List<GuideListItemVO> getTopGuidesByLikes(int limit) {
        return guideMapper.listTopGuidesByLikes(limit)
                .stream()
                .map(this::toGuideListItem)
                .toList();
    }

    /** 将攻略摘要实体转换为列表展示 VO */
    private GuideListItemVO toGuideListItem(GuideSummary entity) {
        return GuideListItemVO.builder()
                .id(entity.getId())
                .destinationId(entity.getDestinationId())
                .destinationName(entity.getDestinationName())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorAvatarUrl(entity.getAuthorAvatarUrl())
                .title(entity.getTitle())
                .summary(entity.getSummary())
                .coverImageUrl(entity.getCoverImageUrl())
                .locationText(entity.getLocationText())
                .scope(entity.getScope())
                .travelMode(entity.getTravelMode())
                .days(entity.getDays())
                .budgetTotal(entity.getBudgetTotal())
                .viewsCount(entity.getViewsCount())
                .likesCount(entity.getLikesCount())
                .commentsCount(entity.getCommentsCount())
                .favoritesCount(entity.getFavoritesCount())
                .publishedAt(entity.getPublishedAt())
                .build();
    }

    /** 将行程天数实体转换为 VO，并挂载该天的景点列表 */
    private GuideDetailVO.ItineraryDay toItineraryDay(GuideItineraryDay day, Map<Long, List<GuideItinerarySpot>> spotGroup) {
        List<GuideDetailVO.ItinerarySpot> daySpots = spotGroup.getOrDefault(day.getId(), Collections.emptyList())
                .stream()
                .map(this::toItinerarySpot)
                .toList();
        return GuideDetailVO.ItineraryDay.builder()
                .id(day.getId())
                .dayNo(day.getDayNo())
                .title(day.getTitle())
                .summary(day.getSummary())
                .spots(daySpots)
                .build();
    }

    /** 将行程景点实体转换为 VO */
    private GuideDetailVO.ItinerarySpot toItinerarySpot(GuideItinerarySpot entity) {
        return GuideDetailVO.ItinerarySpot.builder()
                .id(entity.getId())
                .itineraryDayId(entity.getItineraryDayId())
                .name(entity.getName())
                .description(entity.getDescription())
                .visitTime(entity.getVisitTime())
                .durationMinutes(entity.getDurationMinutes())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    /** 将攻略分类实体转换为 VO，并提取该分类下所有建议的文本 */
    private GuideDetailVO.TipCategory toTipCategory(GuideTipCategory tipCategory, Map<Long, List<GuideTipItem>> tipItemGroup) {
        List<String> items = tipItemGroup.getOrDefault(tipCategory.getId(), Collections.emptyList())
                .stream()
                .map(GuideTipItem::getItemText)
                .filter(Objects::nonNull)
                .toList();
        return GuideDetailVO.TipCategory.builder()
                .id(tipCategory.getId())
                .categoryName(tipCategory.getCategoryName())
                .items(items)
                .build();
    }

    /** 将预算明细实体转换为 VO */
    private GuideDetailVO.BudgetItem toBudgetItem(GuideBudgetItem item) {
        return GuideDetailVO.BudgetItem.builder()
                .id(item.getId())
                .categoryCode(item.getCategoryCode())
                .categoryName(item.getCategoryName())
                .amount(item.getAmount())
                .percentage(item.getPercentage())
                .build();
    }

    /** 将相关攻略实体转换为 VO */
    private GuideDetailVO.RelatedGuide toRelatedGuide(GuideRelated guide) {
        return GuideDetailVO.RelatedGuide.builder()
                .id(guide.getId())
                .title(guide.getTitle())
                .coverImageUrl(guide.getCoverImageUrl())
                .destinationName(guide.getDestinationName())
                .build();
    }

    /** 页码安全处理：null 或小于1 时默认为1 */
    private int normalizePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    /** 每页数量安全处理：默认10条，最大不超过50条 */
    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 50);
    }
}
