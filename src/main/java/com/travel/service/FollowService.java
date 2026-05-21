package com.travel.service;

import com.travel.pojo.model.UserProfile;
import com.travel.pojo.vo.FollowVO;

import java.util.List;

public interface FollowService {
    FollowVO follow(Long followedUserId);
    FollowVO unfollow(Long followedUserId);
    FollowVO getFollowStatus(Long followedUserId);
    List<UserProfile> getFollowers(Long userId);
    List<UserProfile> getFollowing(Long userId);
}
