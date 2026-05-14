package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.dto.UpdateProfileDTO;
import com.travel.pojo.vo.GuideListItemVO;
import com.travel.pojo.vo.UserMeVO;
import com.travel.security.UserContext;
import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;

import com.travel.pojo.model.GuideComment;
import com.travel.pojo.model.GuideSummary;
import com.travel.pojo.model.User;
import com.travel.pojo.model.UserProfile;
import com.travel.mapper.GuideCommentMapper;
import com.travel.mapper.GuideFavoriteMapper;
import com.travel.mapper.GuideLikeMapper;
import com.travel.mapper.GuideMapper;
import com.travel.mapper.UserMapper;
import com.travel.mapper.UserProfileMapper;
import com.travel.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 * 负责用户个人信息的查询和修改
 *
 * 数据模型说明：
 * - User 表：存储账号基础信息（用户名、手机号、邮箱、密码等）
 * - UserProfile 表：存储用户扩展资料（昵称、头像、简介、VIP状态、统计数据等）
 * 两个表通过 userId 关联，查询时需要 JOIN 或分别查询后组装
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserProfileMapper profileMapper;
    private final GuideCommentMapper guideCommentMapper;
    private final GuideLikeMapper guideLikeMapper;
    private final GuideFavoriteMapper guideFavoriteMapper;
    private final GuideMapper guideMapper;

    /**
     * 获取当前登录用户信息
     * 从安全上下文中获取当前登录的用户ID，再查询完整信息
     */
    @Override
    public UserMeVO getCurrentUser() {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return getByUserId(userId);
    }

    /**
     * 根据用户ID获取用户完整信息
     * 分别从 User 和 UserProfile 两张表查询，组装成统一的响应对象
     */
    @Override
    public UserMeVO getByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        UserProfile profile = profileMapper.selectByUserId(userId);
        return toMeResponse(user, profile);
    }

    /**
     * 更新当前登录用户的资料
     * 采用"有则更新，无则插入"的策略：
     * - 如果用户已有 Profile 记录，直接更新
     * - 如果是新用户还没有 Profile，自动创建一条默认记录
     *
     * 事务保证：整个操作在一个事务中，确保数据一致性
     */
    @Override
    @Transactional
    public UserMeVO updateCurrentUserProfile(UpdateProfileDTO request) {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        // 更新 User 表的邮箱字段（空字符串视为 null，避免唯一约束冲突）
        String email = request.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            userMapper.updateUserEmail(userId, email.trim());
        }

        // 查询是否已有用户资料记录
        UserProfile current = profileMapper.selectByUserId(userId);
        if (current == null) {
            // 首次完善资料，创建新的 Profile 记录，设置默认值
            current = new UserProfile();
            current.setUserId(userId);
            current.setIsVip(false);
            current.setGuidesCount(0);
            current.setFollowersCount(0);
            current.setLikesReceivedCount(0);
            current.setNickname(request.getNickname());
            current.setAvatarUrl(request.getAvatarUrl());
            current.setBio(request.getBio());
            profileMapper.insert(current);
        } else {
            // 已有记录，只更新非空的字段，保留原值
            if (request.getNickname() != null) {
                current.setNickname(request.getNickname());
            }
            if (request.getAvatarUrl() != null) {
                current.setAvatarUrl(request.getAvatarUrl());
            }
            if (request.getBio() != null) {
                current.setBio(request.getBio());
            }
            profileMapper.updateProfile(current);
        }

        // 返回更新后的完整用户信息
        return getByUserId(userId);
    }

    @Override
    public PageResult<GuideComment> listMyComments(int page, int pageSize) {
        Long userId = UserContext.requireUserId();
        // 页码处理
        page = Math.max(page, 1);
        pageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);

        int offset = (page - 1) * pageSize;
        var list = guideCommentMapper.selectByUserId(userId, offset, pageSize);
        long total = guideCommentMapper.countByUserId(userId);
        return PageResult.of(list, page, pageSize, total);
    }

    @Override
    public PageResult<GuideListItemVO> listMyLikedGuides(int page, int pageSize) {
        Long userId = UserContext.requireUserId();
        page = Math.max(page, 1);
        pageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);

        int offset = (page - 1) * pageSize;
        var guideIds = guideLikeMapper.selectLikedGuideIds(userId, offset, pageSize);
        long total = guideLikeMapper.countByUserId(userId);
        List<GuideSummary> list = guideIds.isEmpty() ? List.of() : guideMapper.selectByIds(guideIds);
        return PageResult.of(toGuideListItemVOs(list), page, pageSize, total);
    }

    @Override
    public PageResult<GuideListItemVO> listMyFavoriteGuides(int page, int pageSize) {
        Long userId = UserContext.requireUserId();
        page = Math.max(page, 1);
        pageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);

        int offset = (page - 1) * pageSize;
        var guideIds = guideFavoriteMapper.selectFavoritedGuideIds(userId, offset, pageSize);
        long total = guideFavoriteMapper.countByUserId(userId);
        List<GuideSummary> list = guideIds.isEmpty() ? List.of() : guideMapper.selectByIds(guideIds);
        return PageResult.of(toGuideListItemVOs(list), page, pageSize, total);
    }

    private List<GuideListItemVO> toGuideListItemVOs(List<GuideSummary> list) {
        return list.stream().map(g -> {
            GuideListItemVO vo = new GuideListItemVO();
            BeanUtils.copyProperties(g, vo);
            return vo;
        }).toList();
    }

    /**
     * 将数据库实体转换为 API 响应对象
     * 处理 UserProfile 为 null 的情况（新注册用户可能还没有资料）
     */
    private UserMeVO toMeResponse(User user, UserProfile profile) {
        //将User,UserProfile转换成UserMeVO
        return UserMeVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .nickname(profile == null ? user.getUsername() : profile.getNickname())
                .avatarUrl(profile == null ? null : profile.getAvatarUrl())
                .bio(profile == null ? null : profile.getBio())
                .vip(profile != null && Boolean.TRUE.equals(profile.getIsVip()))
                .guidesCount(profile == null ? 0 : profile.getGuidesCount())
                .followersCount(profile == null ? 0 : profile.getFollowersCount())
                .likesReceivedCount(profile == null ? 0 : profile.getLikesReceivedCount())
                .build();
    }
}
