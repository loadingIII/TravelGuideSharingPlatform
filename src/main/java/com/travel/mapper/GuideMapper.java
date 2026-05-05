package com.travel.mapper;

import com.travel.pojo.model.GuideBudgetItem;
import com.travel.pojo.model.GuideDetail;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideItinerarySpot;
import com.travel.pojo.model.GuideRelated;
import com.travel.pojo.model.GuideSummary;
import com.travel.pojo.model.GuideTipCategory;
import com.travel.pojo.model.GuideTipItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
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
     * 根据天数ID列表获取景点列表
     */
    List<GuideItinerarySpot> listItinerarySpotsByDayIds(@Param("dayIds") List<Long> dayIds);

    /**
     * 获取攻略提示分类列表
     */
    List<GuideTipCategory> listTipCategories(@Param("guideId") Long guideId);

    /**
     * 根据提示ID列表获取提示项目列表
     */
    List<GuideTipItem> listTipItemsByTipIds(@Param("tipIds") List<Long> tipIds);

    /**
     * 获取攻略预算项目列表
     */
    List<GuideBudgetItem> listBudgetItems(@Param("guideId") Long guideId);

    /**
     * 获取相关攻略列表
     */
    List<GuideRelated> listRelatedGuides(@Param("guideId") Long guideId, @Param("limit") int limit);

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
}
