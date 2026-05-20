package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;
import com.travel.mapper.GuideStoryMapper;
import com.travel.mapper.StoryCommentLikeMapper;
import com.travel.mapper.StoryCommentMapper;
import com.travel.mapper.UserProfileMapper;
import com.travel.pojo.dto.CreateCommentDTO;
import com.travel.pojo.dto.CreateStoryDTO;
import com.travel.pojo.dto.UpdateStoryDTO;
import com.travel.pojo.model.StoryComment;
import com.travel.pojo.model.UserProfile;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.pojo.vo.StoryCommentVO;
import com.travel.service.GuideStoryService;
import com.travel.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideStoryServiceImpl implements GuideStoryService {

    private final GuideStoryMapper guideStoryMapper;
    private final StoryCommentMapper storyCommentMapper;
    private final StoryCommentLikeMapper storyCommentLikeMapper;
    private final UserProfileMapper userProfileMapper;

    /**
     * 根据ID查询旅行者故事
     */
    @Override
    public GuideStoryVO getStoryById(Long id) {
        GuideStoryVO story = guideStoryMapper.selectById(id);
        if (story == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "故事不存在");
        }
        story.setImages(guideStoryMapper.selectImageUrlsByStoryId(id));
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
        list.forEach(s -> s.setImages(guideStoryMapper.selectImageUrlsByStoryId(s.getId())));
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
        list.forEach(s -> s.setImages(guideStoryMapper.selectImageUrlsByStoryId(s.getId())));
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

        if (dto.getImageUrls() != null) {
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                guideStoryMapper.insertStoryImage(story.getId(), dto.getImageUrls().get(i), i);
            }
        }
        return story.getId();
    }

    @Override
    public void deleteStory(Long storyId) {
        Long userId = UserContext.requireUserId();
        GuideStoryVO existing = guideStoryMapper.selectById(storyId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "故事不存在");
        }
        if (!existing.getAuthorUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的故事");
        }
        guideStoryMapper.deleteImagesByStoryId(storyId);
        storyCommentMapper.deleteByStoryId(storyId);
        guideStoryMapper.deleteById(storyId);
    }

    @Override
    public void updateStory(Long storyId, UpdateStoryDTO dto) {
        Long userId = UserContext.requireUserId();
        GuideStoryVO existing = guideStoryMapper.selectById(storyId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "故事不存在");
        }
        if (!existing.getAuthorUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能编辑自己的故事");
        }
        guideStoryMapper.updateContent(storyId, dto.getContent());

        if (dto.getImageUrls() != null) {
            guideStoryMapper.deleteImagesByStoryId(storyId);
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                guideStoryMapper.insertStoryImage(storyId, dto.getImageUrls().get(i), i);
            }
        }
    }

    @Override
    public Long addStoryComment(Long storyId, CreateCommentDTO dto) {
        Long userId = UserContext.requireUserId();
        UserProfile profile = userProfileMapper.selectByUserId(userId);
        StoryComment comment = new StoryComment();
        comment.setStoryId(storyId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentCommentId(dto.getParentCommentId());
        comment.setAuthorName(profile != null ? profile.getNickname() : "用户");
        comment.setAuthorAvatarUrl(profile != null ? profile.getAvatarUrl() : null);
        storyCommentMapper.insertComment(comment);
        storyCommentMapper.incrementCommentsCount(storyId);
        return comment.getId();
    }

    @Override
    public PageResult<StoryComment> listStoryComments(Long storyId, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        var list = storyCommentMapper.selectByStoryId(storyId, offset, safePageSize);
        long total = storyCommentMapper.countByStoryId(storyId);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    @Override
    public List<StoryCommentVO> listStoryCommentsTree(Long storyId) {
        Long currentUserId = UserContext.requireUserId();
        List<StoryComment> allComments = storyCommentMapper.selectAllByStoryId(storyId);

        Set<Long> likedCommentIds = currentUserId != null
                ? allComments.stream()
                        .map(StoryComment::getId)
                        .filter(id -> storyCommentLikeMapper.exists(id, currentUserId) > 0)
                        .collect(Collectors.toSet())
                : Collections.emptySet();

        Map<Long, List<StoryComment>> replyGroup = allComments.stream()
                .filter(c -> c.getParentCommentId() != null)
                .collect(Collectors.groupingBy(StoryComment::getParentCommentId));

        return allComments.stream()
                .filter(c -> c.getParentCommentId() == null)
                .map(c -> toCommentVO(c, replyGroup, likedCommentIds))
                .toList();
    }

    @Override
    public void deleteStoryComment(Long commentId) {
        Long userId = UserContext.requireUserId();
        Long commentUserId = storyCommentMapper.selectUserIdById(commentId);
        if (commentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }
        if (!commentUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的评论");
        }
        Long storyId = storyCommentMapper.selectStoryIdById(commentId);
        storyCommentMapper.deleteById(commentId);
        if (storyId != null) {
            storyCommentMapper.decrementCommentsCount(storyId);
        }
    }

    private StoryCommentVO toCommentVO(StoryComment comment, Map<Long, List<StoryComment>> replyGroup, Set<Long> likedCommentIds) {
        List<StoryComment> replies = replyGroup.getOrDefault(comment.getId(), Collections.emptyList());
        List<StoryCommentVO> replyVOs = replies.stream()
                .map(r -> toCommentVOWithoutReplies(r, likedCommentIds))
                .toList();

        return StoryCommentVO.builder()
                .id(comment.getId())
                .storyId(comment.getStoryId())
                .userId(comment.getUserId())
                .nickname(comment.getAuthorName())
                .avatarUrl(comment.getAuthorAvatarUrl())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .likesCount(comment.getLikesCount())
                .createdAt(comment.getCreatedAt())
                .liked(likedCommentIds.contains(comment.getId()))
                .replies(replyVOs)
                .build();
    }

    private StoryCommentVO toCommentVOWithoutReplies(StoryComment comment, Set<Long> likedCommentIds) {
        return StoryCommentVO.builder()
                .id(comment.getId())
                .storyId(comment.getStoryId())
                .userId(comment.getUserId())
                .nickname(comment.getAuthorName())
                .avatarUrl(comment.getAuthorAvatarUrl())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .likesCount(comment.getLikesCount())
                .createdAt(comment.getCreatedAt())
                .liked(likedCommentIds.contains(comment.getId()))
                .replies(null)
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
