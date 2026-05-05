package com.travel.mapper;

import com.travel.pojo.model.Destination;
import com.travel.pojo.model.DestinationShowcase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 目的地Mapper接口
 */
@Mapper
public interface DestinationMapper {

    /**
     * 获取目的地列表
     */
    List<Destination> listDestinations(@Param("keyword") String keyword,
                                             @Param("hotSort") Boolean hotSort,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    /**
     * 统计目的地数量
     */
    long countDestinations(@Param("keyword") String keyword);

    /**
     * 根据ID获取目的地
     */
    Destination selectById(@Param("id") Long id);

    /**
     * 获取指定分类的展示列表
     */
    List<DestinationShowcase> listShowcasesBySection(@Param("section") String section, @Param("limit") int limit);

    /**
     * 搜索目的地
     */
    List<Destination> searchDestinations(@Param("query") String query, @Param("limit") int limit);

    /**
     * 获取热度前N的目的地
     */
    List<Destination> listHotDestinations(@Param("limit") int limit);
}
