package com.travel.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GuideFavoriteMapper {

    @Select("SELECT COUNT(*) FROM guide_favorites WHERE guide_id=#{guideId} AND user_id=#{userId}")
    int exists(@Param("guideId") Long guideId, @Param("userId") Long userId);

    @Insert("INSERT INTO guide_favorites (guide_id, user_id) VALUES (#{guideId}, #{userId})")
    int insert(@Param("guideId") Long guideId, @Param("userId") Long userId);

    @Delete("DELETE FROM guide_favorites WHERE guide_id=#{guideId} AND user_id=#{userId}")
    int delete(@Param("guideId") Long guideId, @Param("userId") Long userId);

    @Update("UPDATE guides SET favorites_count = favorites_count + 1 WHERE id = #{guideId}")
    int incrementFavoritesCount(@Param("guideId") Long guideId);

    @Update("UPDATE guides SET favorites_count = favorites_count - 1 WHERE id = #{guideId} AND favorites_count > 0")
    int decrementFavoritesCount(@Param("guideId") Long guideId);

    @Select("SELECT gf.guide_id FROM guide_favorites gf WHERE gf.user_id=#{userId} ORDER BY gf.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Long> selectFavoritedGuideIds(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM guide_favorites WHERE user_id=#{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT favorites_count FROM guides WHERE id = #{guideId}")
    long selectFavoritesCount(@Param("guideId") Long guideId);
}
