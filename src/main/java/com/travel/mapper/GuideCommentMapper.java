package com.travel.mapper;

import com.travel.pojo.model.GuideComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
            "c.likes_count, c.status, c.created_at, u.username AS author_name " +
            "FROM guide_comments c LEFT JOIN users u ON c.user_id = u.id " +
            "<where>" +
            "<if test='status != null'>AND c.status = #{status}</if>" +
            "</where>" +
            "ORDER BY c.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideComment> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM guide_comments c " +
            "<where>" +
            "<if test='status != null'>AND c.status = #{status}</if>" +
            "</where>")
    long countAll(@Param("status") Integer status);

    @Delete("DELETE FROM guide_comments WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE guide_comments SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
