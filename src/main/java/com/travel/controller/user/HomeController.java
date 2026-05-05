package com.travel.controller.user;

import com.travel.common.ApiResponse;

import com.travel.pojo.vo.HomeVO;
import com.travel.pojo.vo.SearchVO;
import com.travel.service.HomeService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 首页控制器
 * 处理首页数据展示和搜索功能
 */
@Validated
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    /**
     * 获取首页数据
     * 返回首页展示所需的所有数据，包括推荐目的地、热门攻略、推荐内容等
     *
     * @return 首页综合数据
     */
    @GetMapping("/home")
    public ApiResponse<HomeVO> home() {
        return ApiResponse.success(homeService.getHome());
    }

    /**
     * 全局搜索
     * 根据关键词搜索攻略、目的地等内容
     * 支持按类型筛选搜索结果
     *
     * @param query 搜索关键词（必填），不能为空
     * @param type  搜索类型（可选），默认 all（全部），可选 guide（攻略）、destination（目的地）
     * @return 搜索结果，包含攻略和目的地的匹配结果
     */
    @GetMapping("/search")
    public ApiResponse<SearchVO> search(@RequestParam("q") @NotBlank(message = "搜索关键词不能为空") String query,
                                        @RequestParam(value = "type", defaultValue = "all") String type) {
        return ApiResponse.success(homeService.search(query, type));
    }

    @GetMapping("/favicon.ico")
    public void favicon(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
