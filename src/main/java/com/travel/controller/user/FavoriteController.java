package com.travel.controller.user;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.vo.FavoriteVO;
import com.travel.security.RequireLogin;
import com.travel.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @RequireLogin
    @GetMapping("/{id}/favorite")
    public ApiResponse<FavoriteVO> getFavoriteStatus(@PathVariable("id") Long id) {
        return ApiResponse.success(favoriteService.getFavoriteStatus(id));
    }

    @RequireLogin
    @PostMapping("/{id}/favorite")
    public ApiResponse<FavoriteVO> favoriteGuide(@PathVariable("id") Long id) {
        return ApiResponse.success(favoriteService.favoriteGuide(id));
    }

    @RequireLogin
    @DeleteMapping("/{id}/favorite")
    public ApiResponse<FavoriteVO> unfavoriteGuide(@PathVariable("id") Long id) {
        return ApiResponse.success(favoriteService.unfavoriteGuide(id));
    }
}
