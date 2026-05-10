package com.travel.controller.user;

import com.travel.common.PageResult;
import java.util.List;
import com.travel.pojo.dto.UpdateProfileDTO;
import com.travel.pojo.model.GuideComment;
import com.travel.pojo.model.GuideSummary;
import com.travel.pojo.vo.GuideListItemVO;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.pojo.vo.UserMeVO;
import com.travel.security.RequireLogin;
import com.travel.common.ApiResponse;
import com.travel.mapper.GuideCommentMapper;
import com.travel.mapper.GuideFavoriteMapper;
import com.travel.mapper.GuideLikeMapper;
import com.travel.mapper.GuideMapper;
import com.travel.security.UserContext;
import com.travel.service.GuideService;
import com.travel.service.GuideStoryService;
import com.travel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户个人信息相关的操作，如查看个人信息、更新个人资料等
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final GuideService guideService;
    private final GuideStoryService guideStoryService;
    private final GuideCommentMapper guideCommentMapper;
    private final GuideLikeMapper guideLikeMapper;
    private final GuideFavoriteMapper guideFavoriteMapper;
    private final GuideMapper guideMapper;

    /**
     * 获取当前用户信息
     * 返回当前登录用户的详细信息，包括个人资料、统计数据等
     * 需要用户登录
     *
     * @return 用户详细信息
     */
    @RequireLogin
    @GetMapping("/me")
    public ApiResponse<UserMeVO> me() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    /**
     * 更新当前用户资料
     * 修改当前登录用户的个人资料信息，如昵称、头像、简介等
     * 需要用户登录
     *
     * @param request 更新请求，包含要修改的资料字段
     * @return 更新后的用户信息
     */
    @RequireLogin
    @PutMapping("/me/profile")
    public ApiResponse<UserMeVO> updateProfile(@Valid @RequestBody UpdateProfileDTO request) {
        return ApiResponse.success(userService.updateCurrentUserProfile(request));
    }

    @RequireLogin
    @GetMapping("/me/guides")
    public ApiResponse<PageResult<GuideListItemVO>> myGuides(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(guideService.listMyGuides(page, pageSize));
    }

    @RequireLogin
    @GetMapping("/me/stories")
    public ApiResponse<PageResult<GuideStoryVO>> myStories(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(guideStoryService.listMyStories(page, pageSize));
    }

    @RequireLogin
    @GetMapping("/me/comments")
    public ApiResponse<PageResult<GuideComment>> myComments(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.requireUserId();
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        int offset = (safePage - 1) * safePageSize;
        var list = guideCommentMapper.selectByUserId(userId, offset, safePageSize);
        long total = guideCommentMapper.countByUserId(userId);
        return ApiResponse.success(PageResult.of(list, safePage, safePageSize, total));
    }

    @RequireLogin
    @GetMapping("/me/liked-guides")
    public ApiResponse<PageResult<GuideListItemVO>> myLikedGuides(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.requireUserId();
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        int offset = (safePage - 1) * safePageSize;
        var guideIds = guideLikeMapper.selectLikedGuideIds(userId, offset, safePageSize);
        long total = guideLikeMapper.countByUserId(userId);
        List<GuideSummary> list = guideIds.isEmpty() ? java.util.List.of() : guideMapper.selectByIds(guideIds);
        return ApiResponse.success(PageResult.of(
                list.stream().map(g -> GuideListItemVO.builder()
                        .id(g.getId()).destinationId(g.getDestinationId()).destinationName(g.getDestinationName())
                        .authorId(g.getAuthorId()).authorName(g.getAuthorName()).authorAvatarUrl(g.getAuthorAvatarUrl())
                        .title(g.getTitle()).summary(g.getSummary()).coverImageUrl(g.getCoverImageUrl())
                        .locationText(g.getLocationText()).scope(g.getScope()).travelMode(g.getTravelMode())
                        .days(g.getDays()).budgetTotal(g.getBudgetTotal()).viewsCount(g.getViewsCount())
                        .likesCount(g.getLikesCount()).commentsCount(g.getCommentsCount()).favoritesCount(g.getFavoritesCount())
                        .publishedAt(g.getPublishedAt()).build()).toList(),
                safePage, safePageSize, total));
    }

    @RequireLogin
    @GetMapping("/me/favorite-guides")
    public ApiResponse<PageResult<GuideListItemVO>> myFavoriteGuides(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.requireUserId();
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        int offset = (safePage - 1) * safePageSize;
        var guideIds = guideFavoriteMapper.selectFavoritedGuideIds(userId, offset, safePageSize);
        long total = guideFavoriteMapper.countByUserId(userId);
        List<GuideSummary> list = guideIds.isEmpty() ? java.util.List.of() : guideMapper.selectByIds(guideIds);
        return ApiResponse.success(PageResult.of(
                list.stream().map(g -> GuideListItemVO.builder()
                        .id(g.getId()).destinationId(g.getDestinationId()).destinationName(g.getDestinationName())
                        .authorId(g.getAuthorId()).authorName(g.getAuthorName()).authorAvatarUrl(g.getAuthorAvatarUrl())
                        .title(g.getTitle()).summary(g.getSummary()).coverImageUrl(g.getCoverImageUrl())
                        .locationText(g.getLocationText()).scope(g.getScope()).travelMode(g.getTravelMode())
                        .days(g.getDays()).budgetTotal(g.getBudgetTotal()).viewsCount(g.getViewsCount())
                        .likesCount(g.getLikesCount()).commentsCount(g.getCommentsCount()).favoritesCount(g.getFavoritesCount())
                        .publishedAt(g.getPublishedAt()).build()).toList(),
                safePage, safePageSize, total));
    }
}
