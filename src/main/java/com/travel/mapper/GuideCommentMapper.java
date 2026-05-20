package com.travel.mapper;

import com.travel.pojo.model.GuideComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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
            "c.likes_count, c.status, c.created_at, COALESCE(up.nickname, u.username) AS author_name, up.avatar_url AS author_avatar_url " +
            "FROM guide_comments c LEFT JOIN user_profiles up ON up.user_id = c.user_id LEFT JOIN users u ON u.id = c.user_id " +
            "WHERE (#{status} IS NULL OR c.status = #{status}) " +
            "ORDER BY c.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideComment> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM guide_comments c WHERE (#{status} IS NULL OR c.status = #{status})")
    long countAll(@Param("status") Integer status);

    @Delete("DELETE FROM guide_comments WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT user_id FROM guide_comments WHERE id = #{id}")
    Long selectUserIdById(@Param("id") Long id);

    @Select("SELECT guide_id FROM guide_comments WHERE id = #{id}")
    Long selectGuideIdById(@Param("id") Long id);

    @Update("UPDATE guides SET comments_count = comments_count - 1 WHERE id = #{guideId}")
    int decrementCommentsCount(@Param("guideId") Long guideId);

    @Update("UPDATE guide_comments SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Insert("INSERT INTO guide_comments (guide_id, user_id, parent_comment_id, author_name, author_avatar_url, content, status) " +
            "VALUES (#{guideId}, #{userId}, #{parentCommentId}, #{authorName}, #{authorAvatarUrl}, #{content}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertComment(GuideComment comment);

    @Update("UPDATE guides SET comments_count = comments_count + 1 WHERE id = #{guideId}")
    int incrementCommentsCount(@Param("guideId") Long guideId);

    @Select("SELECT c.id, c.guide_id, c.user_id, c.content, c.parent_comment_id, " +
            "c.likes_count, c.status, c.created_at, up.nickname AS author_name, up.avatar_url AS author_avatar_url, " +
            "g.title AS guide_title " +
            "FROM guide_comments c " +
            "LEFT JOIN user_profiles up ON up.user_id = c.user_id " +
            "LEFT JOIN guides g ON c.guide_id = g.id " +
            "WHERE c.user_id = #{userId} AND c.is_deleted = 0 " +
            "ORDER BY c.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideComment> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM guide_comments WHERE user_id = #{userId} AND is_deleted = 0")
    long countByUserId(@Param("userId") Long userId);

    @Update("UPDATE guide_comments SET is_deleted = 1 WHERE id = #{id}")
    int softDeleteById(@Param("id") Long id);
}
