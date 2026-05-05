package com.travel.controller.user;

import com.travel.common.ApiResponse;

import com.travel.common.PageResult;
import com.travel.pojo.vo.DestinationItemVO;
import com.travel.pojo.vo.GuideListItemVO;
import com.travel.service.DestinationService;

import java.util.List;
import com.travel.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 目的地控制器
 * 处理旅游目的地相关的操作，如目的地列表查询、目的地详情、目的地下的攻略列表等
 */
@RestController
@RequestMapping("/destinations")
@RequiredArgsConstructor
public class DestinationController {
    private final DestinationService destinationService;
    private final GuideService guideService;

    /**
     * 获取目的地列表
     * 支持按关键词搜索、按热度筛选，返回分页的目的地列表
     *
     * @param keyword  搜索关键词（可选），用于搜索目的地名称、国家、城市等
     * @param hot      是否只返回热门目的地（默认false），true时返回热门目的地
     * @param page     页码（可选），默认第1页
     * @param pageSize 每页数量（可选），默认20条
     * @return 分页的目的地列表
     */
    @GetMapping
    public ApiResponse<PageResult<DestinationItemVO>> listDestinations(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "hot", defaultValue = "false") boolean hot,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return ApiResponse.success(destinationService.listDestinations(keyword, hot, page, pageSize));
    }

    /**
     * 获取热度前4的目的地
     * 按popularity_score降序排列，返回前4条
     *
     * @return 热度前4的目的地列表
     */
    @GetMapping("/hot")
    public ApiResponse<List<DestinationItemVO>> hotDestinations() {
        return ApiResponse.success(destinationService.listHotDestinations(4));
    }

    /**
     * 获取目的地详情
     * 根据目的地ID返回目的地的完整信息，包括描述、统计数据等
     *
     * @param id 目的地ID
     * @return 目的地详细信息
     */
    @GetMapping("/{id}")
    public ApiResponse<DestinationItemVO> destinationDetail(@PathVariable("id") Long id) {
        return ApiResponse.success(destinationService.getDestination(id));
    }

    /**
     * 获取目的地下的攻略列表
     * 返回指定目的地下的所有攻略，支持分页
     *
     * @param id       目的地ID
     * @param page     页码（可选），默认第1页
     * @param pageSize 每页数量（可选），默认20条
     * @return 分页的攻略列表
     */
    @GetMapping("/{id}/guides")
    public ApiResponse<PageResult<GuideListItemVO>> destinationGuides(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return ApiResponse.success(guideService.listGuidesByDestination(id, page, pageSize));
    }
}
