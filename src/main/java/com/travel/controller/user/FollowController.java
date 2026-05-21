package com.travel.controller.user;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.model.UserProfile;
import com.travel.pojo.vo.FollowVO;
import com.travel.security.RequireLogin;
import com.travel.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @RequireLogin
    @GetMapping("/{id}/follow")
    public ApiResponse<FollowVO> getFollowStatus(@PathVariable("id") Long id) {
        return ApiResponse.success(followService.getFollowStatus(id));
    }

    @RequireLogin
    @PostMapping("/{id}/follow")
    public ApiResponse<FollowVO> followUser(@PathVariable("id") Long id) {
        return ApiResponse.success(followService.follow(id));
    }

    @RequireLogin
    @DeleteMapping("/{id}/follow")
    public ApiResponse<FollowVO> unfollowUser(@PathVariable("id") Long id) {
        return ApiResponse.success(followService.unfollow(id));
    }

    @GetMapping("/{id}/followers")
    public ApiResponse<List<UserProfile>> getFollowers(@PathVariable("id") Long id) {
        return ApiResponse.success(followService.getFollowers(id));
    }

    @GetMapping("/{id}/following")
    public ApiResponse<List<UserProfile>> getFollowing(@PathVariable("id") Long id) {
        return ApiResponse.success(followService.getFollowing(id));
    }
}
