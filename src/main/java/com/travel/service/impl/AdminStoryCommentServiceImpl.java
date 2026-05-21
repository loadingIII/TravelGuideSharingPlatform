package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.mapper.StoryCommentMapper;
import com.travel.pojo.dto.ReviewResult;
import com.travel.pojo.model.StoryComment;
import com.travel.service.AdminStoryCommentService;
import com.travel.service.AuditLogService;
import com.travel.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 故事评论管理服务实现
 * 封装旅行故事评论的分页查询、审核、删除等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AdminStoryCommentServiceImpl implements AdminStoryCommentService {

    private final StoryCommentMapper storyCommentMapper;
    private final AuditLogService auditLogService;
    private final ReviewService reviewService;
    private static final int PAGE_SIZE = 10;

    @Override
    public PageResult<StoryComment> list(int page, Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyCommentMapper.countAll(status);
        return PageResult.of(storyCommentMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total);
    }

    @Override
    public void delete(Long id, HttpSession session, HttpServletRequest request) {
        storyCommentMapper.deleteById(id);
        auditLogService.log(session, request, "DELETE", "STORY_COMMENT", id, "删除故事评论");
    }

    @Override
    public void audit(Long id, Map<String, String> body, HttpSession session, HttpServletRequest request) {
        String action = body.get("action");
        ReviewResult result = reviewService.resolveAction(action);
        if (result == null) {
            throw new IllegalArgumentException("无效的操作");
        }
        storyCommentMapper.updateStatus(id, result.getStatus());
        auditLogService.log(session, request, "AUDIT", "STORY_COMMENT", id, result.getDetail());
    }
}
