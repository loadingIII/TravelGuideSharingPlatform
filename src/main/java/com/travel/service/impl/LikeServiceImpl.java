package com.travel.service.impl;

import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;
import com.travel.mapper.GuideCommentLikeMapper;
import com.travel.mapper.GuideLikeMapper;
import com.travel.mapper.StoryCommentLikeMapper;
import com.travel.mapper.StoryLikeMapper;
import com.travel.pojo.vo.LikeVO;
import com.travel.security.UserContext;
import com.travel.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final GuideLikeMapper guideLikeMapper;
    private final StoryLikeMapper storyLikeMapper;
    private final GuideCommentLikeMapper guideCommentLikeMapper;
    private final StoryCommentLikeMapper storyCommentLikeMapper;

    @Override
    @Transactional
    public LikeVO likeGuide(Long guideId) {
        Long userId = requireUserId();
        if (guideLikeMapper.exists(guideId, userId) == 0) {
            guideLikeMapper.insert(guideId, userId);
            guideLikeMapper.incrementLikesCount(guideId);
        }
        return LikeVO.builder()
                .liked(true)
                .likesCount(guideLikeMapper.selectLikesCount(guideId))
                .build();
    }

    @Override
    @Transactional
    public LikeVO unlikeGuide(Long guideId) {
        Long userId = requireUserId();
        if (guideLikeMapper.exists(guideId, userId) > 0) {
            guideLikeMapper.delete(guideId, userId);
            guideLikeMapper.decrementLikesCount(guideId);
        }
        return LikeVO.builder()
                .liked(false)
                .likesCount(guideLikeMapper.selectLikesCount(guideId))
                .build();
    }

    @Override
    public LikeVO getGuideLikeStatus(Long guideId) {
        Long userId = UserContext.requireUserId();
        boolean liked = userId != null && guideLikeMapper.exists(guideId, userId) > 0;
        return LikeVO.builder()
                .liked(liked)
                .likesCount(guideLikeMapper.selectLikesCount(guideId))
                .build();
    }

    @Override
    @Transactional
    public LikeVO likeStory(Long storyId) {
        Long userId = requireUserId();
        if (storyLikeMapper.exists(storyId, userId) == 0) {
            storyLikeMapper.insert(storyId, userId);
            storyLikeMapper.incrementLikesCount(storyId);
        }
        return LikeVO.builder()
                .liked(true)
                .likesCount(storyLikeMapper.selectLikesCount(storyId))
                .build();
    }

    @Override
    @Transactional
    public LikeVO unlikeStory(Long storyId) {
        Long userId = requireUserId();
        if (storyLikeMapper.exists(storyId, userId) > 0) {
            storyLikeMapper.delete(storyId, userId);
            storyLikeMapper.decrementLikesCount(storyId);
        }
        return LikeVO.builder()
                .liked(false)
                .likesCount(storyLikeMapper.selectLikesCount(storyId))
                .build();
    }

    @Override
    public LikeVO getStoryLikeStatus(Long storyId) {
        Long userId = UserContext.requireUserId();
        boolean liked = userId != null && storyLikeMapper.exists(storyId, userId) > 0;
        return LikeVO.builder()
                .liked(liked)
                .likesCount(storyLikeMapper.selectLikesCount(storyId))
                .build();
    }

    @Override
    @Transactional
    public LikeVO likeComment(Long commentId) {
        Long userId = requireUserId();
        if (guideCommentLikeMapper.exists(commentId, userId) == 0) {
            guideCommentLikeMapper.insert(commentId, userId);
            guideCommentLikeMapper.incrementLikesCount(commentId);
        }
        return LikeVO.builder()
                .liked(true)
                .likesCount(guideCommentLikeMapper.selectLikesCount(commentId))
                .build();
    }

    @Override
    @Transactional
    public LikeVO unlikeComment(Long commentId) {
        Long userId = requireUserId();
        if (guideCommentLikeMapper.exists(commentId, userId) > 0) {
            guideCommentLikeMapper.delete(commentId, userId);
            guideCommentLikeMapper.decrementLikesCount(commentId);
        }
        return LikeVO.builder()
                .liked(false)
                .likesCount(guideCommentLikeMapper.selectLikesCount(commentId))
                .build();
    }

    @Override
    @Transactional
    public LikeVO likeStoryComment(Long commentId) {
        Long userId = requireUserId();
        if (storyCommentLikeMapper.exists(commentId, userId) == 0) {
            storyCommentLikeMapper.insert(commentId, userId);
            storyCommentLikeMapper.incrementLikesCount(commentId);
        }
        return LikeVO.builder()
                .liked(true)
                .likesCount(storyCommentLikeMapper.selectLikesCount(commentId))
                .build();
    }

    @Override
    @Transactional
    public LikeVO unlikeStoryComment(Long commentId) {
        Long userId = requireUserId();
        if (storyCommentLikeMapper.exists(commentId, userId) > 0) {
            storyCommentLikeMapper.delete(commentId, userId);
            storyCommentLikeMapper.decrementLikesCount(commentId);
        }
        return LikeVO.builder()
                .liked(false)
                .likesCount(storyCommentLikeMapper.selectLikesCount(commentId))
                .build();
    }

    private Long requireUserId() {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }
}
