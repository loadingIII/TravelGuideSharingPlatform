package com.travel.controller.user;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.vo.LikeVO;
import com.travel.security.RequireLogin;
import com.travel.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @RequireLogin
    @PostMapping("/guides/{id}/like")
    public ApiResponse<LikeVO> likeGuide(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.likeGuide(id));
    }

    @RequireLogin
    @DeleteMapping("/guides/{id}/like")
    public ApiResponse<LikeVO> unlikeGuide(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.unlikeGuide(id));
    }

    @RequireLogin
    @GetMapping("/guides/{id}/like")
    public ApiResponse<LikeVO> getGuideLikeStatus(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.getGuideLikeStatus(id));
    }

    @RequireLogin
    @PostMapping("/stories/{id}/like")
    public ApiResponse<LikeVO> likeStory(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.likeStory(id));
    }

    @RequireLogin
    @DeleteMapping("/stories/{id}/like")
    public ApiResponse<LikeVO> unlikeStory(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.unlikeStory(id));
    }

    @RequireLogin
    @GetMapping("/stories/{id}/like")
    public ApiResponse<LikeVO> getStoryLikeStatus(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.getStoryLikeStatus(id));
    }

    @RequireLogin
    @PostMapping("/guides/comments/{id}/like")
    public ApiResponse<LikeVO> likeComment(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.likeComment(id));
    }

    @RequireLogin
    @DeleteMapping("/guides/comments/{id}/like")
    public ApiResponse<LikeVO> unlikeComment(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.unlikeComment(id));
    }

    @RequireLogin
    @PostMapping("/stories/comments/{id}/like")
    public ApiResponse<LikeVO> likeStoryComment(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.likeStoryComment(id));
    }

    @RequireLogin
    @DeleteMapping("/stories/comments/{id}/like")
    public ApiResponse<LikeVO> unlikeStoryComment(@PathVariable("id") Long id) {
        return ApiResponse.success(likeService.unlikeStoryComment(id));
    }
}
