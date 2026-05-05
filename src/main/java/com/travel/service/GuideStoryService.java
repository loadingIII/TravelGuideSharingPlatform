package com.travel.service;

import com.travel.common.PageResult;
import com.travel.pojo.vo.GuideStoryVO;

/**
 * 旅行者故事服务接口
 */
public interface GuideStoryService {

    /**
     * 根据ID查询旅行者故事
     *
     * @param id 故事ID
     * @return 故事详情
     */
    GuideStoryVO getStoryById(Long id);

    /**
     * 根据用户ID查询旅行者故事
     *
     * @param userId 用户ID
     * @param page   页码
     * @param pageSize 每页数量
     * @return 分页的故事列表
     */
    PageResult<GuideStoryVO> getStoriesByUserId(Long userId, Integer page, Integer pageSize);

    /**
     * 分页查询旅行者故事
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return 分页的故事列表
     */
    PageResult<GuideStoryVO> listStories(Integer page, Integer pageSize);
}
