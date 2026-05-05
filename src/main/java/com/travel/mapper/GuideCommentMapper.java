package com.travel.mapper;

import com.travel.pojo.model.GuideComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    @Select("SELECT c.id, c.guide_id, c.user_id, c.content, c.parent_comment_id, " +
            "c.likes_count, c.created_at, u.username AS author_name " +
            "FROM guide_comments c LEFT JOIN users u ON c.user_id = u.id " +
            "ORDER BY c.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideComment> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM guide_comments")
    long countAll();

    @Delete("DELETE FROM guide_comments WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
