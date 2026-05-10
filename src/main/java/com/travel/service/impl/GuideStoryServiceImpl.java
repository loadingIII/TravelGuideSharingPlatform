package com.travel.service.impl;

import com.travel.common.PageResult;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ErrorCode;
import com.travel.mapper.GuideStoryMapper;
import com.travel.pojo.dto.CreateStoryDTO;
import com.travel.pojo.dto.UpdateStoryDTO;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.service.GuideStoryService;
import com.travel.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 旅行者故事服务实现类
 * 负责旅行者故事的查询和分页展示
 *
 * 旅行者故事是用户分享的旅行经历和感悟，与攻略（Guide）不同，
 * 故事更偏向个人叙事和情感分享，攻略更偏向实用信息和行程规划
 *
 * 核心功能：
 * - 根据ID查询单个故事
 * - 按用户ID分页查询（用于个人主页）
 * - 分页查询所有故事（用于故事列表页）
 */
@Service
@RequiredArgsConstructor
public class GuideStoryServiceImpl implements GuideStoryService {

    private final GuideStoryMapper guideStoryMapper;

    /**
     * 根据ID查询旅行者故事
     */
    @Override
    public GuideStoryVO getStoryById(Long id) {
        GuideStoryVO story = guideStoryMapper.selectById(id);
        if (story == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "故事不存在");
        }
        return story;
    }

    /**
     * 根据用户ID分页查询旅行者故事
     * 用于用户个人主页展示该用户发布的所有故事
     */
    @Override
    public PageResult<GuideStoryVO> getStoriesByUserId(Long userId, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        List<GuideStoryVO> list = guideStoryMapper.selectByUserId(userId, offset, safePageSize);
        long total = guideStoryMapper.countByUserId(userId);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    /**
     * 分页查询所有旅行者故事
     * 用于故事广场/列表页展示
     */
    @Override
    public PageResult<GuideStoryVO> listStories(Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        List<GuideStoryVO> list = guideStoryMapper.selectAll(offset, safePageSize, null);
        long total = guideStoryMapper.countAll(null);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    @Override
    public PageResult<GuideStoryVO> listMyStories(Integer page, Integer pageSize) {
        Long userId = UserContext.requireUserId();
        return getStoriesByUserId(userId, page, pageSize);
    }

    @Override
    public Long createStory(CreateStoryDTO dto) {
        Long userId = UserContext.requireUserId();
        GuideStoryVO story = new GuideStoryVO();
        story.setAuthorUserId(userId);
        story.setContent(dto.getContent());
        story.setStatus(0);
        guideStoryMapper.insert(story);
        return story.getId();
    }

    @Override
    public void deleteStory(Long storyId) {
        Long userId = UserContext.requireUserId();
        GuideStoryVO existing = guideStoryMapper.selectById(storyId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "故事不存在");
        }
        guideStoryMapper.deleteById(storyId);
    }

    @Override
    public void updateStory(Long storyId, UpdateStoryDTO dto) {
        Long userId = UserContext.requireUserId();
        GuideStoryVO existing = guideStoryMapper.selectById(storyId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "故事不存在");
        }
        guideStoryMapper.updateContent(storyId, dto.getContent());
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
