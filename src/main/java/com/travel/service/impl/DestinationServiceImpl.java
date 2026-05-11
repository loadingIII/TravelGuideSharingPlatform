package com.travel.service.impl;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.model.Destination;
import com.travel.pojo.vo.DestinationItemVO;
import com.travel.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 目的地服务实现类
 * 负责旅游目的地相关的业务逻辑处理
 */
@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {
    private final DestinationMapper destinationMapper;

    /**
     * 分页获取目的地列表
     * 支持按关键词模糊搜索和热门筛选
     *
     * @param keyword  搜索关键词，可选，为空则查询全部
     * @param hot      是否只返回热门目的地
     * @param page     页码（从1开始）
     * @param pageSize 每页数量
     */
    @Override
    public PageResult<DestinationItemVO> listDestinations(String keyword, boolean hot, Integer page, Integer pageSize) {
        // 参数安全处理：页码默认1，每页数量默认4，最大50
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize; // 计算数据库偏移量

        // 查询数据并转换为 VO 对象
        List<DestinationItemVO> list = destinationMapper.listDestinations(keyword, hot, offset, safePageSize)
                .stream()
                .map(this::toDestinationItem)
                .toList();

        // 查询总条数（用于分页计算）
        long total = destinationMapper.countDestinations(keyword);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    /**
     * 获取单个目的地详情
     */
    @Override
    public DestinationItemVO getDestination(Long id) {
        Destination entity = destinationMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "目的地不存在");
        }
        return toDestinationItem(entity);
    }

    /**
     * 搜索目的地
     * 根据关键词模糊匹配目的地名称，返回指定数量的结果
     */
    @Override
    public List<DestinationItemVO> searchDestinations(String query, int limit) {
        return destinationMapper.searchDestinations(query, limit)
                .stream()
                .map(this::toDestinationItem)
                .toList();
    }

    /**
     * 获取热门目的地列表
     * 按热度分数排序返回前 N 个目的地
     */
    @Override
    public List<DestinationItemVO> listHotDestinations(int limit) {
        return destinationMapper.listHotDestinations(limit)
                .stream()
                .map(this::toDestinationItem)
                .toList();
    }

    /**
     * 将目的地数据库实体转换为 API 响应对象
     */
    private DestinationItemVO toDestinationItem(Destination entity) {
        return DestinationItemVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .country(entity.getCountry())
                .city(entity.getCity())
                .description(entity.getDescription())
                .coverImageUrl(entity.getCoverImageUrl())
                .rating(entity.getRatingAvg())
                .guidesCount(entity.getGuidesCount())
                .travelersCount(entity.getTravelersCount())
                .popularityScore(entity.getPopularityScore())
                .build();
    }

    /** 页码安全处理：null 或小于1 时默认为1 */
    private int normalizePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    /** 每页数量安全处理：默认4条，最大不超过50条 */
    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 4;
        }
        return Math.min(pageSize, 50);
    }
}
