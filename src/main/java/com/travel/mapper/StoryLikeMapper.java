package com.travel.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StoryLikeMapper {

    @Select("SELECT COUNT(*) FROM traveler_story_likes WHERE story_id=#{storyId} AND user_id=#{userId}")
    int exists(@Param("storyId") Long storyId, @Param("userId") Long userId);

    @Insert("INSERT INTO traveler_story_likes (story_id, user_id) VALUES (#{storyId}, #{userId})")
    int insert(@Param("storyId") Long storyId, @Param("userId") Long userId);

    @Delete("DELETE FROM traveler_story_likes WHERE story_id=#{storyId} AND user_id=#{userId}")
    int delete(@Param("storyId") Long storyId, @Param("userId") Long userId);

    @Update("UPDATE traveler_stories SET likes_count = likes_count + 1 WHERE id = #{storyId}")
    int incrementLikesCount(@Param("storyId") Long storyId);

    @Update("UPDATE traveler_stories SET likes_count = likes_count - 1 WHERE id = #{storyId} AND likes_count > 0")
    int decrementLikesCount(@Param("storyId") Long storyId);

    @Select("SELECT likes_count FROM traveler_stories WHERE id = #{storyId}")
    long selectLikesCount(@Param("storyId") Long storyId);
}
