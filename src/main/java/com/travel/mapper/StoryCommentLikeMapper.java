package com.travel.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StoryCommentLikeMapper {

    @Select("SELECT COUNT(*) FROM traveler_story_comment_likes WHERE comment_id=#{commentId} AND user_id=#{userId}")
    int exists(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Insert("INSERT INTO traveler_story_comment_likes (comment_id, user_id) VALUES (#{commentId}, #{userId})")
    int insert(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Delete("DELETE FROM traveler_story_comment_likes WHERE comment_id=#{commentId} AND user_id=#{userId}")
    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Update("UPDATE traveler_story_comments SET likes_count = likes_count + 1 WHERE id = #{commentId}")
    int incrementLikesCount(@Param("commentId") Long commentId);

    @Update("UPDATE traveler_story_comments SET likes_count = likes_count - 1 WHERE id = #{commentId} AND likes_count > 0")
    int decrementLikesCount(@Param("commentId") Long commentId);

    @Select("SELECT likes_count FROM traveler_story_comments WHERE id = #{commentId}")
    long selectLikesCount(@Param("commentId") Long commentId);
}
