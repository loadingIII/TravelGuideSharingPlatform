package com.travel.service.impl;

import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;
import com.travel.mapper.GuideCommentLikeMapper;
import com.travel.mapper.GuideCommentMapper;
import com.travel.mapper.GuideMapper;
import com.travel.mapper.UserProfileMapper;
import com.travel.pojo.dto.CreateCommentDTO;
import com.travel.pojo.model.GuideComment;
import com.travel.pojo.model.UserProfile;
import com.travel.pojo.vo.GuideCommentVO;
import com.travel.security.UserContext;
import com.travel.service.GuideCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 攻略评论服务实现类
 * 负责将数据库中的扁平评论列表组装成树形结构
 *
 * 评论层级说明：
 * - 一级评论：parent_comment_id 为 NULL，直接对攻略的评论
 * - 二级回复：parent_comment_id 指向一级评论 ID，是对评论的回复
 * 本实现只支持两级结构（评论 + 回复），不支持回复的回复
 */
@Service
@RequiredArgsConstructor
public class GuideCommentServiceImpl implements GuideCommentService {

    private final GuideCommentMapper guideCommentMapper;
    private final GuideMapper guideMapper;
    private final UserProfileMapper userProfileMapper;
    private final GuideCommentLikeMapper guideCommentLikeMapper;

    /**
     * 获取攻略的评论树形列表
     *
     * 组装逻辑：
     * 1. 从数据库一次性查出该攻略的所有评论（扁平列表）
     * 2. 按 parentCommentId 分组，NULL 的为一级评论，非 NULL 的为回复
     * 3. 将回复列表挂载到对应的一级评论下
     */
    @Override
    public List<GuideCommentVO> listCommentsByGuideId(Long guideId) {
        // 1. 查询所有评论（扁平列表，包含评论者昵称和头像）
        List<GuideComment> allComments = guideCommentMapper.listByGuideId(guideId);

        // 2. 批量获取当前用户已点赞的评论ID集合（未登录则为空）
        Long currentUserId = UserContext.requireUserId();
        Set<Long> likedCommentIds;
        if (currentUserId != null && !allComments.isEmpty()) {
            String commentIds = allComments.stream()
                    .map(c -> c.getId().toString())
                    .collect(Collectors.joining(","));
            Set<Long> result = guideCommentLikeMapper.selectLikedCommentIdsByCommentIds(commentIds, currentUserId);
            likedCommentIds = result != null ? result : Collections.emptySet();
        } else {
            likedCommentIds = Collections.emptySet();
        }

        // 3. 按 parentCommentId 分组：key=null 的是一级评论，key=评论ID 的是该评论的回复
        Map<Long, List<GuideComment>> replyGroup = allComments.stream()
                .filter(c -> c.getParentCommentId() != null)
                .collect(Collectors.groupingBy(GuideComment::getParentCommentId));

        // 4. 筛选出一级评论，组装树形结构返回
        return allComments.stream()
                .filter(c -> c.getParentCommentId() == null)
                .map(c -> toCommentVO(c, replyGroup, likedCommentIds))
                .toList();
    }

    @Override
    @Transactional
    public GuideCommentVO addComment(Long guideId, CreateCommentDTO dto) {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        UserProfile profile = userProfileMapper.selectByUserId(userId);
        GuideComment comment = new GuideComment();
        comment.setGuideId(guideId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentCommentId(dto.getParentCommentId());
        comment.setAuthorName(profile != null ? profile.getNickname() : "用户");
        comment.setAuthorAvatarUrl(profile != null ? profile.getAvatarUrl() : null);

        guideCommentMapper.insertComment(comment);
        guideCommentMapper.incrementCommentsCount(guideId);

        return GuideCommentVO.builder()
                .id(comment.getId())
                .guideId(guideId)
                .userId(userId)
                .nickname(comment.getAuthorName())
                .avatarUrl(comment.getAuthorAvatarUrl())
                .content(dto.getContent())
                .parentCommentId(dto.getParentCommentId())
                .likesCount(0)
                .createdAt(comment.getCreatedAt())
                .liked(false)
                .replies(null)
                .build();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        Long commentUserId = guideCommentMapper.selectUserIdById(commentId);
        if (commentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }
        if (!commentUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的评论");
        }
        Long guideId = guideCommentMapper.selectGuideIdById(commentId);
        guideCommentMapper.softDeleteById(commentId);
        if (guideId != null) {
            guideCommentMapper.decrementCommentsCount(guideId);
        }
    }

    /**
     * 将数据库实体转换为 VO，并挂载该评论的回复列表
     */
    private GuideCommentVO toCommentVO(GuideComment comment, Map<Long, List<GuideComment>> replyGroup, Set<Long> likedCommentIds) {
        // 获取该评论下的所有回复，如果没有则返回空列表
        List<GuideComment> replies = replyGroup.getOrDefault(comment.getId(), Collections.emptyList());
        List<GuideCommentVO> replyVOs = replies.stream()
                .map(r -> toCommentVOWithoutReplies(r, likedCommentIds))
                .toList();

        return GuideCommentVO.builder()
                .id(comment.getId())
                .guideId(comment.getGuideId())
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

    /**
     * 将数据库实体转换为 VO（不带回复列表，用于二级回复）
     */
    private GuideCommentVO toCommentVOWithoutReplies(GuideComment comment, Set<Long> likedCommentIds) {
        return GuideCommentVO.builder()
                .id(comment.getId())
                .guideId(comment.getGuideId())
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
}
