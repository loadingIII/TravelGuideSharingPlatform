package com.travel.service.impl;

import com.travel.common.PageResult;
import com.travel.mapper.GuideStoryMapper;
import com.travel.pojo.dto.ReviewResult;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.service.AdminAuthService;
import com.travel.service.AdminStoryService;
import com.travel.service.AuditLogService;
import com.travel.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStoryServiceImpl implements AdminStoryService {

    private final GuideStoryMapper storyMapper;
    private final AdminAuthService adminAuthService;
    private final AuditLogService auditLogService;
    private final ReviewService reviewService;
    private static final int PAGE_SIZE = 10;

    @Override
    public PageResult<GuideStoryVO> listStories(int page, Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyMapper.countAll(status);
        return PageResult.of(storyMapper.selectAll(offset, PAGE_SIZE, status), page, PAGE_SIZE, total);
    }

    @Override
    public GuideStoryVO getStoryDetail(Long id) {
        return storyMapper.selectById(id);
    }

    @Override
    public void createStory(Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        GuideStoryVO story = new GuideStoryVO();
        story.setAuthorUserId(body.get("authorUserId") != null ? Long.valueOf(body.get("authorUserId").toString()) : null);
        story.setAuthorName((String) body.get("authorName"));
        story.setContent((String) body.get("content"));
        story.setStatus(0);
        storyMapper.insert(story);
        auditLogService.log(session, request, "CREATE", "STORY", story.getId(), "新增故事");
    }

    @Override
    public void updateStory(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        GuideStoryVO story = storyMapper.selectById(id);
        if (story == null) throw new RuntimeException("故事不存在");
        storyMapper.updateContent(id, (String) body.get("content"));
        auditLogService.log(session, request, "UPDATE", "STORY", id, "编辑故事");
    }

    @Override
    public void auditStory(Long id, String action, HttpSession session, HttpServletRequest request) {
        GuideStoryVO story = storyMapper.selectById(id);
        if (story == null) throw new RuntimeException("故事不存在");

        ReviewResult result = reviewService.resolveAction(action);
        if (result == null) throw new RuntimeException("无效的操作");

        storyMapper.updateStatus(id, result.getStatus());
        auditLogService.log(session, request, "AUDIT", "STORY", id, result.getDetail());
    }

    @Override
    public void deleteStory(Long id, HttpSession session, HttpServletRequest request) {
        storyMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "STORY", id, "删除故事");
    }
}
