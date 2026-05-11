package com.travel.service;


import com.travel.pojo.common.PageResult;
import com.travel.pojo.dto.UpdateProfileDTO;
import com.travel.pojo.model.GuideComment;
import com.travel.pojo.vo.GuideListItemVO;
import com.travel.pojo.vo.UserMeVO;

/**
 * 用户服务接口
 * 处理用户个人信息相关的业务逻辑
 */
public interface UserService {

    /**
     * 获取当前登录用户的信息
     *
     * @return 用户详细信息
     */
    UserMeVO getCurrentUser();

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    UserMeVO getByUserId(Long userId);

    /**
     * 更新当前登录用户的资料
     *
     * @param request 更新请求，包含要修改的资料字段
     * @return 更新后的用户信息
     */
    UserMeVO updateCurrentUserProfile(UpdateProfileDTO request);

    /**
     * 分页获取当前登录用户的评论
     * @param page
     * @param pageSize
     * @return `
     */
    PageResult<GuideComment> listMyComments(int page, int pageSize);

    PageResult<GuideListItemVO> listMyLikedGuides(int page, int pageSize);

    PageResult<GuideListItemVO> listMyFavoriteGuides(int page, int pageSize);
}
