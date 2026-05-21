package com.travel.service;

import com.travel.pojo.vo.LikeVO;

public interface LikeService {
    LikeVO likeGuide(Long guideId);
    LikeVO unlikeGuide(Long guideId);
    LikeVO getGuideLikeStatus(Long guideId);
    LikeVO likeStory(Long storyId);
    LikeVO unlikeStory(Long storyId);
    LikeVO getStoryLikeStatus(Long storyId);
    LikeVO likeComment(Long commentId);
    LikeVO unlikeComment(Long commentId);
    LikeVO likeStoryComment(Long commentId);
    LikeVO unlikeStoryComment(Long commentId);
}
