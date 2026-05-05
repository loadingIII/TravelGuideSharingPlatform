package com.travel.service.impl;


import com.travel.pojo.vo.*;
import com.travel.service.DestinationService;
import com.travel.service.GuideService;
import com.travel.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 首页服务实现类
 * 负责聚合首页展示所需的各类数据，以及全局搜索功能
 *
 * 设计说明：
 * - 首页数据来自多个服务（目的地、攻略），由本服务统一聚合后返回
 * - 搜索功能支持按类型筛选，可同时搜索攻略和目的地
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final DestinationService destinationService;
    private final GuideService guideService;

    /**
     * 获取首页完整数据
     * 一次性返回首页需要的所有展示数据，减少前端请求次数
     *
     * 数据组成：
     * - recommended: 推荐目的地（6条）
     * - popular: 热门目的地（6条）
     * - inspiration: 灵感目的地（6条）
     * - communityPicks: 社区精选攻略（3条）
     */
    @Override
    public HomeVO getHome() {
        List<ShowcaseItemVO> recommended = destinationService.listShowcasesBySection("recommended", 6);
        List<ShowcaseItemVO> popular = destinationService.listShowcasesBySection("popular", 6);
        List<ShowcaseItemVO> inspiration = destinationService.listShowcasesBySection("inspiration", 6);
        List<GuideListItemVO> communityPicks = guideService.listCommunityPicks(3);
        return new HomeVO(recommended, popular, inspiration, communityPicks);
    }

    /**
     * 全局搜索
     * 根据关键词同时搜索攻略和目的地，每类最多返回10条结果
     *
     * @param query 搜索关键词
     * @param type  搜索类型：all-搜索全部，guide-仅搜攻略，destination-仅搜目的地
     */
    @Override
    public SearchVO search(String query, String type) {
        // 空关键词直接返回空结果，避免不必要的数据库查询
        if (!StringUtils.hasText(query)) {
            return new SearchVO(Collections.emptyList(), Collections.emptyList());
        }

        // 统一转小写，null 默认为 "all"
        String normalized = type == null ? "all" : type.toLowerCase();
        List<DestinationItemVO> destinations = Collections.emptyList();
        List<GuideListItemVO> guides = Collections.emptyList();

        // 根据搜索类型决定查询哪些内容
        if ("all".equals(normalized) || "destination".equals(normalized)) {
            destinations = destinationService.searchDestinations(query, 10);
        }
        if ("all".equals(normalized) || "guide".equals(normalized)) {
            guides = guideService.searchGuides(query, 10);
        }

        return new SearchVO(destinations, guides);
    }
}
