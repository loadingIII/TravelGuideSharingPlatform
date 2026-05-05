package com.travel.mapper;

import com.travel.pojo.model.GuideComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 攻略评论 Mapper 接口
 */
@Mapper
public interface GuideCommentMapper {

    /**
     * 查询攻略的所有评论（包含回复）
     * 按创建时间正序排列
     */
    List<GuideComment> listByGuideId(@Param("guideId") Long guideId);

    /**
     * 统计攻略的评论总数
     */
    long countByGuideId(@Param("guideId") Long guideId);
}
