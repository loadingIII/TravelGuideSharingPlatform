package com.travel.mapper;

import com.travel.pojo.model.GuideSummary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GuideLikeMapper {

    @Select("SELECT COUNT(*) FROM guide_likes WHERE guide_id=#{guideId} AND user_id=#{userId}")
    int exists(@Param("guideId") Long guideId, @Param("userId") Long userId);

    @Insert("INSERT INTO guide_likes (guide_id, user_id) VALUES (#{guideId}, #{userId})")
    int insert(@Param("guideId") Long guideId, @Param("userId") Long userId);

    @Delete("DELETE FROM guide_likes WHERE guide_id=#{guideId} AND user_id=#{userId}")
    int delete(@Param("guideId") Long guideId, @Param("userId") Long userId);

    @Update("UPDATE guides SET likes_count = likes_count + 1 WHERE id = #{guideId}")
    int incrementLikesCount(@Param("guideId") Long guideId);

    @Update("UPDATE guides SET likes_count = likes_count - 1 WHERE id = #{guideId} AND likes_count > 0")
    int decrementLikesCount(@Param("guideId") Long guideId);

    @Select("SELECT gl.guide_id FROM guide_likes gl WHERE gl.user_id=#{userId} ORDER BY gl.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Long> selectLikedGuideIds(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM guide_likes WHERE user_id=#{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT likes_count FROM guides WHERE id = #{guideId}")
    long selectLikesCount(@Param("guideId") Long guideId);
}
