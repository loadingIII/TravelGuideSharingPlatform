package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.vo.DestinationItemVO;


import java.util.List;

/**
 * 目的地服务接口
 * 处理旅游目的地相关的业务逻辑
 */
public interface DestinationService {

    /**
     * 获取目的地列表
     * 支持按关键词搜索、按热度筛选
     *
     * @param keyword  搜索关键词（可选）
     * @param hot      是否只返回热门目的地
     * @param page     页码
     * @param pageSize 每页数量
     * @return 分页的目的地列表
     */
    PageResult<DestinationItemVO> listDestinations(String keyword, boolean hot, Integer page, Integer pageSize);

    /**
     * 获取目的地详情
     *
     * @param id 目的地ID
     * @return 目的地详细信息
     */
    DestinationItemVO getDestination(Long id);

    /**
     * 搜索目的地
     *
     * @param query 搜索关键词
     * @param limit 返回数量
     * @return 目的地列表
     */
    List<DestinationItemVO> searchDestinations(String query, int limit);

    /**
     * 获取热度前N的目的地
     *
     * @param limit 返回数量
     * @return 热度排名前N的目的地列表
     */
    List<DestinationItemVO> listHotDestinations(int limit);
}
