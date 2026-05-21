package com.travel.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

@Mapper
public interface GuideCommentLikeMapper {

    @Select("SELECT COUNT(*) FROM guide_comment_likes WHERE comment_id=#{commentId} AND user_id=#{userId}")
    int exists(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Insert("INSERT INTO guide_comment_likes (comment_id, user_id) VALUES (#{commentId}, #{userId})")
    int insert(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Delete("DELETE FROM guide_comment_likes WHERE comment_id=#{commentId} AND user_id=#{userId}")
    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Update("UPDATE guide_comments SET likes_count = likes_count + 1 WHERE id = #{commentId}")
    int incrementLikesCount(@Param("commentId") Long commentId);

    @Update("UPDATE guide_comments SET likes_count = likes_count - 1 WHERE id = #{commentId} AND likes_count > 0")
    int decrementLikesCount(@Param("commentId") Long commentId);

    @Select("SELECT likes_count FROM guide_comments WHERE id = #{commentId}")
    long selectLikesCount(@Param("commentId") Long commentId);

    @Select("SELECT comment_id FROM guide_comment_likes WHERE comment_id IN (${commentIds}) AND user_id = #{userId}")
    Set<Long> selectLikedCommentIdsByCommentIds(@Param("commentIds") String commentIds, @Param("userId") Long userId);
}
