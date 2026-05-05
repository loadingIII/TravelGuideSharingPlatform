package com.travel.mapper;

import com.travel.pojo.model.StoryComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 故事评论 Mapper 接口
 */
@Mapper
public interface StoryCommentMapper {

    @Select("SELECT c.id, c.story_id, c.user_id, c.content, c.parent_comment_id, " +
            "c.likes_count, c.status, c.created_at, u.username AS author_name " +
            "FROM traveler_story_comments c LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE (#{status} IS NULL OR c.status = #{status}) " +
            "ORDER BY c.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<StoryComment> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM traveler_story_comments c WHERE (#{status} IS NULL OR c.status = #{status})")
    long countAll(@Param("status") Integer status);

    @Delete("DELETE FROM traveler_story_comments WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE traveler_story_comments SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
