package com.travel.service;

import com.travel.common.PageResult;
import com.travel.pojo.vo.GuideDetailVO;
import com.travel.pojo.vo.GuideListItemVO;


import java.util.List;

/**
 * 攻略服务接口
 * 处理旅游攻略相关的业务逻辑
 */
public interface GuideService {

    /**
     * 获取攻略列表
     * 支持按关键词、旅行范围、旅行方式、排序方式等条件筛选
     *
     * @param keyword    搜索关键词（可选）
     * @param scope      旅行范围（可选），如 domestic 或 international
     * @param travelMode 旅行方式（可选），如 free、group、family、honeymoon
     * @param sort       排序方式，默认 latest
     * @param page       页码
     * @param pageSize   每页数量
     * @return 分页的攻略列表
     */
    PageResult<GuideListItemVO> listGuides(String keyword, String scope, String travelMode, String sort, Integer page, Integer pageSize);

    /**
     * 获取指定目的地下的攻略列表
     *
     * @param destinationId 目的地ID
     * @param page          页码
     * @param pageSize      每页数量
     * @return 分页的攻略列表
     */
    PageResult<GuideListItemVO> listGuidesByDestination(Long destinationId, Integer page, Integer pageSize);

    /**
     * 获取攻略详情
     * 返回攻略的完整信息，包括行程安排、预算明细、评论等
     *
     * @param guideId 攻略ID
     * @return 攻略详细信息
     */
    GuideDetailVO getGuideDetail(Long guideId);

    /**
     * 获取社区精选攻略
     *
     * @param limit 返回数量
     * @return 攻略列表
     */
    List<GuideListItemVO> listCommunityPicks(int limit);

    /**
     * 搜索攻略
     *
     * @param query 搜索关键词
     * @param limit 返回数量
     * @return 攻略列表
     */
    List<GuideListItemVO> searchGuides(String query, int limit);

    /**
     * 按点赞数获取热门攻略
     *
     * @param limit 返回数量
     * @return 攻略列表
     */
    List<GuideListItemVO> getTopGuidesByLikes(int limit);
}
