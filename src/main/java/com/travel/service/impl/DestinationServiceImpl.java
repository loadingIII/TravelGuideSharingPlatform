package com.travel.service.impl;

import com.travel.common.PageResult;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ErrorCode;

import com.travel.pojo.model.Destination;
import com.travel.pojo.model.DestinationShowcase;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.vo.DestinationItemVO;
import com.travel.pojo.vo.ShowcaseItemVO;
import com.travel.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 目的地服务实现类
 * 负责旅游目的地的查询、搜索和展示逻辑
 *
 * 核心功能：
 * - 分页查询目的地列表（支持关键词搜索和热门筛选）
 * - 按分类获取展示数据（推荐、热门、灵感）
 * - 目的地搜索和热度排行
 *
 * 数据说明：
 * - Destination: 目的地基础信息（名称、国家、城市、评分等）
 * - DestinationShowcase: 目的地展示信息（展示标题、描述、图片等，用于首页展示卡片）
 */
@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {
    private final DestinationMapper destinationMapper;

    /**
     * 分页获取目的地列表
     * 支持按关键词模糊搜索和热门筛选
     *
     * @param keyword  搜索关键词，可选，为空则查询全部
     * @param hot      是否只返回热门目的地
     * @param page     页码（从1开始）
     * @param pageSize 每页数量
     */
    @Override
    public PageResult<DestinationItemVO> listDestinations(String keyword, boolean hot, Integer page, Integer pageSize) {
        // 参数安全处理：页码默认1，每页数量默认4，最大50
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize; // 计算数据库偏移量

        // 查询数据并转换为 VO 对象
        List<DestinationItemVO> list = destinationMapper.listDestinations(keyword, hot, offset, safePageSize)
                .stream()
                .map(this::toDestinationItem)
                .toList();

        // 查询总条数（用于分页计算）
        long total = destinationMapper.countDestinations(keyword);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    /**
     * 获取单个目的地详情
     */
    @Override
    public DestinationItemVO getDestination(Long id) {
        Destination entity = destinationMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "目的地不存在");
        }
        return toDestinationItem(entity);
    }

    /**
     * 获取指定分类的展示列表
     * 用于首页等场景的卡片展示，section 对应 DestinationShowcase 表中的分类
     *
     * @param section 分类：recommended（推荐）、popular（热门）、inspiration（灵感）
     * @param limit   返回数量
     */
    @Override
    public List<ShowcaseItemVO> listShowcasesBySection(String section, int limit) {
        return destinationMapper.listShowcasesBySection(section, limit)
                .stream()
                .map(this::toShowcaseItem)
                .toList();
    }

    /**
     * 搜索目的地
     * 根据关键词模糊匹配目的地名称，返回指定数量的结果
     */
    @Override
    public List<DestinationItemVO> searchDestinations(String query, int limit) {
        return destinationMapper.searchDestinations(query, limit)
                .stream()
                .map(this::toDestinationItem)
                .toList();
    }

    /**
     * 获取热门目的地列表
     * 按热度分数排序返回前 N 个目的地
     */
    @Override
    public List<DestinationItemVO> listHotDestinations(int limit) {
        return destinationMapper.listHotDestinations(limit)
                .stream()
                .map(this::toDestinationItem)
                .toList();
    }

    /**
     * 将目的地数据库实体转换为 API 响应对象
     */
    private DestinationItemVO toDestinationItem(Destination entity) {
        return DestinationItemVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .country(entity.getCountry())
                .city(entity.getCity())
                .description(entity.getDescription())
                .coverImageUrl(entity.getCoverImageUrl())
                .rating(entity.getRatingAvg())
                .guidesCount(entity.getGuidesCount())
                .travelersCount(entity.getTravelersCount())
                .popularityScore(entity.getPopularityScore())
                .build();
    }

    /**
     * 将展示信息实体转换为展示卡片 VO
     */
    private ShowcaseItemVO toShowcaseItem(DestinationShowcase entity) {
        return ShowcaseItemVO.builder()
                .destinationId(entity.getDestinationId())
                .title(entity.getDisplayTitle())
                .subtitle(entity.getDisplaySubtitle())
                .description(entity.getDisplayDescription())
                .imageUrl(entity.getDisplayImageUrl())
                .rating(entity.getDisplayRating())
                .build();
    }

    /** 页码安全处理：null 或小于1 时默认为1 */
    private int normalizePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    /** 每页数量安全处理：默认4条，最大不超过50条 */
    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 4;
        }
        return Math.min(pageSize, 50);
    }
}
