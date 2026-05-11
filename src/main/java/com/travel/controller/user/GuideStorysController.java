package com.travel.controller.user;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.common.PageResult;
import com.travel.pojo.dto.CreateCommentDTO;
import com.travel.pojo.dto.CreateStoryDTO;
import com.travel.pojo.dto.UpdateStoryDTO;
import com.travel.pojo.model.StoryComment;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.security.RequireLogin;
import com.travel.service.GuideStoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * 旅行者故事控制器
 */
@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class GuideStorysController {

    private final GuideStoryService guideStoryService;

    /**
     * 根据ID查询旅行者故事
     *
     * @param id 故事ID
     * @return 故事详情
     */
    @GetMapping("/{id}")
    public ApiResponse<GuideStoryVO> getStoryById(@PathVariable("id") Long id) {
        return ApiResponse.success(guideStoryService.getStoryById(id));
    }

    /**
     * 根据用户ID查询旅行者故事
     *
     * @param userId   用户ID
     * @param page     页码（可选），默认第1页
     * @param pageSize 每页数量（可选），默认5条
     * @return 分页的故事列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResult<GuideStoryVO>> getStoriesByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        return ApiResponse.success(guideStoryService.getStoriesByUserId(userId, page, pageSize));
    }

    /**
     * 分页查询旅行者故事
     *
     * @param page     页码（可选），默认第1页
     * @param pageSize 每页数量（可选），默认5条
     * @return 分页的故事列表
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<GuideStoryVO>> listStories(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        return ApiResponse.success(guideStoryService.listStories(page, pageSize));
    }

    @RequireLogin
    @PostMapping
    public ApiResponse<Long> createStory(@Valid @RequestBody CreateStoryDTO dto) {
        return ApiResponse.success(guideStoryService.createStory(dto));
    }

    @RequireLogin
    @PutMapping("/{id}")
    public ApiResponse<Void> updateStory(@PathVariable("id") Long id, @Valid @RequestBody UpdateStoryDTO dto) {
        guideStoryService.updateStory(id, dto);
        return ApiResponse.success();
    }

    @RequireLogin
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStory(@PathVariable("id") Long id) {
        guideStoryService.deleteStory(id);
        return ApiResponse.success();
    }

    @RequireLogin
    @PostMapping("/{storyId}/comments")
    public ApiResponse<Long> addStoryComment(@PathVariable("storyId") Long storyId, @Valid @RequestBody CreateCommentDTO dto) {
        return ApiResponse.success(guideStoryService.addStoryComment(storyId, dto));
    }

    @GetMapping("/{storyId}/comments")
    public ApiResponse<PageResult<StoryComment>> listStoryComments(
            @PathVariable("storyId") Long storyId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(guideStoryService.listStoryComments(storyId, page, pageSize));
    }
}
