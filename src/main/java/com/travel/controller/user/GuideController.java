package com.travel.controller.user;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;

import java.util.List;

import com.travel.pojo.dto.CreateCommentDTO;
import com.travel.pojo.dto.CreateGuideDTO;
import com.travel.pojo.dto.UpdateGuideDTO;
import com.travel.pojo.vo.GuideCommentVO;
import com.travel.pojo.vo.GuideDetailVO;
import com.travel.pojo.vo.GuideListItemVO;
import com.travel.security.RequireLogin;
import com.travel.service.GuideCommentService;
import com.travel.service.GuideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 攻略控制器
 * 处理旅游攻略相关的操作，如攻略列表查询、攻略详情查看等
 */
@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {
    private static final Logger log = LoggerFactory.getLogger(GuideController.class);
    private final GuideService guideService;
    private final GuideCommentService guideCommentService;

    /**
     * 获取攻略列表
     * 支持按关键词、旅行范围、旅行方式、排序方式等条件筛选攻略
     * 返回分页的攻略列表
     *
     * @param keyword    搜索关键词（可选），用于搜索攻略标题、摘要等
     * @param scope      旅行范围（可选），如 domestic（国内）或 international（国际）
     * @param travelMode 旅行方式（可选），如 free（自由行）、group（跟团游）、family（家庭游）、honeymoon（蜜月游）
     * @param sort       排序方式（可选），默认 latest（最新），可选 popular（最热）
     * @param page       页码（可选），默认第1页
     * @param pageSize   每页数量（可选），默认20条
     * @return 分页的攻略列表
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<GuideListItemVO>> listGuides(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "travelMode", required = false) String travelMode,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        return ApiResponse.success(guideService.listGuides(keyword, scope, travelMode, sort, page, pageSize));
    }

    @GetMapping("/ranking/likes")
    public ApiResponse<List<GuideListItemVO>> topGuidesByLikes() {
        return ApiResponse.success(guideService.getTopGuidesByLikes(6));
    }

    /**
     * 获取攻略详情
     * 根据攻略ID返回攻略的完整信息，包括行程安排、预算明细、评论等
     *
     * @param id 攻略ID
     * @return 攻略详细信息
     */
    @GetMapping("/{id}")
    public ApiResponse<GuideDetailVO> guideDetail(@PathVariable("id") Long id) {
        return ApiResponse.success(guideService.getGuideDetail(id));
    }

    /**
     * 获取攻略评论列表
     * 返回树形结构的评论数据：一级评论 + 每条下的回复
     *
     * @param id 攻略ID
     * @return 评论列表（一级评论携带 replies 子列表）
     */
    @GetMapping("/comments/{id}")
    public ApiResponse<List<GuideCommentVO>> guideComments(@PathVariable("id") Long id) {
        log.info("获取攻略评论列表");
        return ApiResponse.success(guideCommentService.listCommentsByGuideId(id));
    }

    @RequireLogin
    @PostMapping
    public ApiResponse<Long> createGuide(@Valid @RequestBody CreateGuideDTO dto) {
        return ApiResponse.success(guideService.createGuide(dto));
    }

    @RequireLogin
    @PutMapping("/{id}")
    public ApiResponse<Void> updateGuide(@PathVariable("id") Long id, @Valid @RequestBody UpdateGuideDTO dto) {
        guideService.updateGuide(id, dto);
        return ApiResponse.success();
    }

    @RequireLogin
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGuide(@PathVariable("id") Long id) {
        guideService.deleteGuide(id);
        return ApiResponse.success();
    }

    @RequireLogin
    @PostMapping("/{guideId}/comments")
    public ApiResponse<GuideCommentVO> addComment(@PathVariable("guideId") Long guideId, @Valid @RequestBody CreateCommentDTO dto) {
        return ApiResponse.success(guideCommentService.addComment(guideId, dto));
    }

    @RequireLogin
    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable("id") Long id) {
        guideCommentService.deleteComment(id);
        return ApiResponse.success();
    }
}
