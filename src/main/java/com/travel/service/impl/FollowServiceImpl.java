package com.travel.service.impl;

import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;
import com.travel.mapper.UserFollowMapper;
import com.travel.pojo.model.UserProfile;
import com.travel.pojo.vo.FollowVO;
import com.travel.security.UserContext;
import com.travel.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserFollowMapper userFollowMapper;

    @Override
    @Transactional
    public FollowVO follow(Long followedUserId) {
        Long followerUserId = requireUserId();
        if (followerUserId.equals(followedUserId)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "不能关注自己");
        }
        if (userFollowMapper.exists(followerUserId, followedUserId) == 0) {
            userFollowMapper.insert(followerUserId, followedUserId);
            userFollowMapper.incrementFollowersCount(followedUserId);
        }
        return FollowVO.builder()
                .following(true)
                .followersCount(userFollowMapper.selectFollowersCount(followedUserId))
                .build();
    }

    @Override
    @Transactional
    public FollowVO unfollow(Long followedUserId) {
        Long followerUserId = requireUserId();
        if (userFollowMapper.exists(followerUserId, followedUserId) > 0) {
            userFollowMapper.delete(followerUserId, followedUserId);
            userFollowMapper.decrementFollowersCount(followedUserId);
        }
        return FollowVO.builder()
                .following(false)
                .followersCount(userFollowMapper.selectFollowersCount(followedUserId))
                .build();
    }

    @Override
    public FollowVO getFollowStatus(Long followedUserId) {
        Long followerUserId = UserContext.requireUserId();
        boolean following = followerUserId != null && userFollowMapper.exists(followerUserId, followedUserId) > 0;
        return FollowVO.builder()
                .following(following)
                .followersCount(userFollowMapper.selectFollowersCount(followedUserId))
                .build();
    }

    @Override
    public List<UserProfile> getFollowers(Long userId) {
        return userFollowMapper.selectFollowers(userId);
    }

    @Override
    public List<UserProfile> getFollowing(Long userId) {
        return userFollowMapper.selectFollowing(userId);
    }

    private Long requireUserId() {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }
}
