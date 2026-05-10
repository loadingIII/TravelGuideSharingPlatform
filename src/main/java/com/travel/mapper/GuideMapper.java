package com.travel.mapper;

import com.travel.pojo.model.GuideDetail;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideSummary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 攻略Mapper接口
 */
@Mapper
public interface GuideMapper {

    /**
     * 获取攻略列表
     */
    List<GuideSummary> listGuides(@Param("keyword") String keyword,
                                        @Param("scope") String scope,
                                        @Param("travelMode") String travelMode,
                                        @Param("sort") String sort,
                                        @Param("offset") int offset,
                                        @Param("pageSize") int pageSize);

    /**
     * 统计攻略数量
     */
    long countGuides(@Param("keyword") String keyword,
                     @Param("scope") String scope,
                     @Param("travelMode") String travelMode);

    /**
     * 获取指定目的地的攻略列表
     */
    List<GuideSummary> listGuidesByDestination(@Param("destinationId") Long destinationId,
                                                     @Param("offset") int offset,
                                                     @Param("pageSize") int pageSize);

    /**
     * 统计指定目的地的攻略数量
     */
    long countGuidesByDestination(@Param("destinationId") Long destinationId);

    /**
     * 获取攻略详情
     */
    GuideDetail selectGuideDetail(@Param("guideId") Long guideId);

    /**
     * 获取攻略行程天数列表
     */
    List<GuideItineraryDay> listItineraryDays(@Param("guideId") Long guideId);



    /**
     * 获取社区精选攻略列表
     */
    List<GuideSummary> listCommunityPicks(@Param("limit") int limit);

    /**
     * 搜索攻略
     */
    List<GuideSummary> searchGuides(@Param("query") String query, @Param("limit") int limit);

    /**
     * 按点赞数获取热门攻略
     */
    List<GuideSummary> listTopGuidesByLikes(@Param("limit") int limit);

    @Select("SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, " +
            "g.location_text, g.scope, g.travel_mode, g.published_at, g.days, g.budget_total, " +
            "g.views_count, g.likes_count, g.comments_count, g.favorites_count, g.status, " +
            "d.name AS destination_name, u.username AS author_name " +
            "FROM guides g LEFT JOIN destinations d ON g.destination_id = d.id " +
            "LEFT JOIN users u ON g.author_id = u.id " +
            "WHERE (#{status} IS NULL OR g.status = #{status}) " +
            "ORDER BY g.published_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideSummary> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM guides g WHERE (#{status} IS NULL OR g.status = #{status})")
    long countAll(@Param("status") Integer status);

    @Select("SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, " +
            "g.location_text, g.scope, g.travel_mode, g.published_at, g.days, g.budget_total, " +
            "g.views_count, g.likes_count, g.comments_count, g.favorites_count, " +
            "d.name AS destination_name, u.username AS author_name " +
            "FROM guides g LEFT JOIN destinations d ON g.destination_id = d.id " +
            "LEFT JOIN users u ON g.author_id = u.id WHERE g.id = #{id}")
    GuideSummary selectById(@Param("id") Long id);

    @Update("UPDATE guides SET title=#{title}, summary=#{summary} WHERE id=#{id}")
    int updateGuide(@Param("id") Long id, @Param("title") String title, @Param("summary") String summary);

    @Delete("DELETE FROM guides WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE guides SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Insert("INSERT INTO guides (destination_id, author_id, title, summary, content_html, cover_image_url, " +
            "location_text, scope, travel_mode, days, budget_total, published_at, status, publish_status) " +
            "VALUES (#{destinationId}, #{authorId}, #{title}, #{summary}, #{contentHtml}, #{coverImageUrl}, " +
            "#{locationText}, #{scope}, #{travelMode}, #{days}, #{budgetTotal}, #{publishedAt}, 0, 'published')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFull(GuideSummary guide);

    @Insert("INSERT INTO guides (destination_id, author_id, title, summary, content_html, cover_image_url, status, publish_status) VALUES (#{destinationId}, #{authorId}, #{title}, #{summary}, #{contentHtml}, #{coverImageUrl}, #{status}, 'draft')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GuideSummary guide);

    @Select("SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, " +
            "g.location_text, g.scope, g.travel_mode, g.published_at, g.days, g.budget_total, " +
            "g.views_count, g.likes_count, g.comments_count, g.favorites_count, g.status, " +
            "d.name AS destination_name, u.username AS author_name " +
            "FROM guides g LEFT JOIN destinations d ON g.destination_id = d.id " +
            "LEFT JOIN users u ON g.author_id = u.id " +
            "WHERE g.author_id = #{authorId} ORDER BY g.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideSummary> selectByAuthorId(@Param("authorId") Long authorId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM guides WHERE author_id = #{authorId}")
    long countByAuthorId(@Param("authorId") Long authorId);

    @Update("UPDATE guides SET title=#{title}, summary=#{summary}, content_html=#{contentHtml}, " +
            "cover_image_url=#{coverImageUrl}, location_text=#{locationText}, scope=#{scope}, travel_mode=#{travelMode} WHERE id=#{id}")
    int updateGuideFull(@Param("id") Long id, @Param("title") String title, @Param("summary") String summary,
                        @Param("contentHtml") String contentHtml, @Param("coverImageUrl") String coverImageUrl,
                        @Param("locationText") String locationText, @Param("scope") String scope,
                        @Param("travelMode") String travelMode);

    @Update("UPDATE guides SET title=#{title}, summary=#{summary}, " +
            "content_html=#{contentHtml}, cover_image_url=#{coverImageUrl}, location_text=#{locationText}, scope=#{scope}, travel_mode=#{travelMode} WHERE id=#{id}")
    int updateGuideMetadata(@Param("id") Long id, @Param("title") String title, @Param("summary") String summary,
                            @Param("contentHtml") String contentHtml, @Param("coverImageUrl") String coverImageUrl,
                            @Param("locationText") String locationText, @Param("scope") String scope,
                            @Param("travelMode") String travelMode);

    @Select("<script>" +
            "SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, " +
            "g.location_text, g.scope, g.travel_mode, g.published_at, g.days, g.budget_total, " +
            "g.views_count, g.likes_count, g.comments_count, g.favorites_count, g.status, " +
            "d.name AS destination_name, u.username AS author_name " +
            "FROM guides g LEFT JOIN destinations d ON g.destination_id = d.id " +
            "LEFT JOIN users u ON g.author_id = u.id " +
            "WHERE g.id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " ORDER BY g.created_at DESC" +
            "</script>")
    List<GuideSummary> selectByIds(@Param("ids") List<Long> ids);

    @Insert("INSERT INTO guide_itinerary_days (guide_id, day_no, title, summary, sort_order) " +
            "VALUES (#{guideId}, #{dayNo}, #{title}, #{summary}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertItineraryDay(GuideItineraryDay day);

    @Delete("DELETE FROM guide_itinerary_days WHERE guide_id = #{guideId}")
    int deleteItineraryDaysByGuideId(@Param("guideId") Long guideId);
}
