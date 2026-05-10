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

    @Insert("INSERT INTO traveler_story_comments (story_id, user_id, parent_comment_id, author_name, author_avatar_url, content, status) " +
            "VALUES (#{storyId}, #{userId}, #{parentCommentId}, #{authorName}, #{authorAvatarUrl}, #{content}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertComment(StoryComment comment);

    @Update("UPDATE traveler_stories SET comments_count = comments_count + 1 WHERE id = #{storyId}")
    int incrementCommentsCount(@Param("storyId") Long storyId);

    @Select("SELECT c.id, c.story_id, c.user_id, c.content, c.parent_comment_id, " +
            "c.likes_count, c.status, c.created_at, c.author_name, c.author_avatar_url " +
            "FROM traveler_story_comments c " +
            "WHERE c.story_id = #{storyId} AND c.is_deleted = 0 " +
            "ORDER BY c.created_at ASC LIMIT #{offset}, #{pageSize}")
    List<StoryComment> selectByStoryId(@Param("storyId") Long storyId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM traveler_story_comments WHERE story_id = #{storyId} AND is_deleted = 0")
    long countByStoryId(@Param("storyId") Long storyId);
}
