package com.travel.mapper;

import com.travel.pojo.model.GuideDetail;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideSummary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 攻略Mapper接口
 * 提供攻略的增删改查操作，包括列表查询、详情查询、作者查询等功能
 */
@Mapper
public interface GuideMapper {

    /**
     * 获取攻略列表（支持关键词搜索、范围筛选、旅行方式筛选、排序）
     *
     * @param keyword     关键词（搜索标题、摘要、内容）
     * @param scope       范围筛选（如：国内、国外）
     * @param travelMode  旅行方式筛选（如：自驾、跟团）
     * @param sort        排序方式（hot=热门，其他按时间）
     * @param offset      偏移量（分页）
     * @param pageSize    每页数量
     * @return 攻略摘要列表
     */
    List<GuideSummary> listGuides(@Param("keyword") String keyword,
                                        @Param("scope") String scope,
                                        @Param("travelMode") String travelMode,
                                        @Param("sort") String sort,
                                        @Param("offset") int offset,
                                        @Param("pageSize") int pageSize);

    /**
     * 统计攻略数量（支持关键词、范围、旅行方式筛选）
     *
     * @param keyword     关键词
     * @param scope       范围
     * @param travelMode  旅行方式
     * @return 攻略总数
     */
    long countGuides(@Param("keyword") String keyword,
                     @Param("scope") String scope,
                     @Param("travelMode") String travelMode);

    /**
     * 获取指定目的地的攻略列表
     *
     * @param destinationId 目的地ID
     * @param offset       偏移量
     * @param pageSize     每页数量
     * @return 攻略摘要列表
     */
    List<GuideSummary> listGuidesByDestination(@Param("destinationId") Long destinationId,
                                                     @Param("offset") int offset,
                                                     @Param("pageSize") int pageSize);

    /**
     * 统计指定目的地的攻略数量
     *
     * @param destinationId 目的地ID
     * @return 攻略总数
     */
    long countGuidesByDestination(@Param("destinationId") Long destinationId);

    /**
     * 获取攻略详情（包含内容HTML）
     *
     * @param guideId 攻略ID
     * @return 攻略详情对象
     */
    GuideDetail selectGuideDetail(@Param("guideId") Long guideId);

    /**
     * 获取攻略行程天数列表
     *
     * @param guideId 攻略ID
     * @return 行程天数列表
     */
    List<GuideItineraryDay> listItineraryDays(@Param("guideId") Long guideId);

    /**
     * 获取社区精选攻略列表（按热度排序）
     *
     * @param limit 返回数量限制
     * @return 攻略摘要列表
     */
    List<GuideSummary> listCommunityPicks(@Param("limit") int limit);

    /**
     * 搜索攻略（支持标题、摘要、内容模糊搜索）
     *
     * @param query 搜索关键词
     * @param limit 返回数量限制
     * @return 攻略摘要列表
     */
    List<GuideSummary> searchGuides(@Param("query") String query, @Param("limit") int limit);

    /**
     * 按点赞数获取热门攻略
     *
     * @param limit 返回数量限制
     * @return 攻略摘要列表
     */
    List<GuideSummary> listTopGuidesByLikes(@Param("limit") int limit);

    /**
     * 分页查询攻略列表（管理员用，可按状态筛选）
     *
     * @param offset   偏移量
     * @param pageSize 每页数量
     * @param status   政略状态（null表示查询全部）
     * @return 攻略摘要列表
     */
    List<GuideSummary> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    /**
     * 统计攻略总数（管理员用，可按状态筛选）
     *
     * @param status 攻略状态（null表示查询全部）
     * @return 攻略总数
     */
    long countAll(@Param("status") Integer status);

    /**
     * 根据ID查询攻略摘要
     *
     * @param id 攻略ID
     * @return 攻略摘要对象
     */
    GuideSummary selectById(@Param("id") Long id);

    /**
     * 更新攻略基本信息（标题、摘要）
     *
     * @param id      攻略ID
     * @param title   标题
     * @param summary 摘要
     * @return 影响行数
     */
    int updateGuide(@Param("id") Long id, @Param("title") String title, @Param("summary") String summary);

    /**
     * 删除攻略
     *
     * @param id 攻略ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新攻略状态（上架/下架）
     *
     * @param id     攻略ID
     * @param status 状态值
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 插入完整攻略（已发布状态）
     *
     * @param guide 攻略对象
     * @return 影响行数（自增ID会回填到guide对象）
     */
    int insertFull(GuideSummary guide);

    /**
     * 插入攻略草稿
     *
     * @param guide 攻略对象
     * @return 影响行数（自增ID会回填到guide对象）
     */
    int insert(GuideSummary guide);

    /**
     * 根据作者ID查询攻略列表
     *
     * @param authorId 作者用户ID
     * @param offset   偏移量
     * @param pageSize 每页数量
     * @return 攻略摘要列表
     */
    List<GuideSummary> selectByAuthorId(@Param("authorId") Long authorId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 统计指定作者的攻略数量
     *
     * @param authorId 作者用户ID
     * @return 攻略总数
     */
    long countByAuthorId(@Param("authorId") Long authorId);

    /**
     * 完整更新攻略信息
     *
     * @param id           攻略ID
     * @param title        标题
     * @param summary      摘要
     * @param contentHtml  内容HTML
     * @param coverImageUrl 封面图URL
     * @param locationText 地点文本
     * @param scope        范围
     * @param travelMode   旅行方式
     * @return 影响行数
     */
    int updateGuideFull(@Param("id") Long id, @Param("title") String title, @Param("summary") String summary,
                        @Param("contentHtml") String contentHtml, @Param("coverImageUrl") String coverImageUrl,
                        @Param("locationText") String locationText, @Param("scope") String scope,
                        @Param("travelMode") String travelMode);

    /**
     * 更新攻略元数据（标题、摘要、内容、图片、地点、范围、旅行方式）
     *
     * @param id           攻略ID
     * @param title        标题
     * @param summary      摘要
     * @param contentHtml  内容HTML
     * @param coverImageUrl 封面图URL
     * @param locationText 地点文本
     * @param scope        范围
     * @param travelMode   旅行方式
     * @return 影响行数
     */
    int updateGuideMetadata(@Param("id") Long id, @Param("title") String title, @Param("summary") String summary,
                            @Param("contentHtml") String contentHtml, @Param("coverImageUrl") String coverImageUrl,
                            @Param("locationText") String locationText, @Param("scope") String scope,
                            @Param("travelMode") String travelMode);

    /**
     * 根据ID列表批量查询攻略
     *
     * @param ids 攻略ID列表
     * @return 攻略摘要列表
     */
    List<GuideSummary> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 插入攻略行程天数
     *
     * @param day 行程天数对象
     * @return 影响行数（自增ID会回填到day对象）
     */
    int insertItineraryDay(GuideItineraryDay day);

    /**
     * 删除指定攻略的所有行程天数
     *
     * @param guideId 攻略ID
     * @return 影响行数
     */
    int deleteItineraryDaysByGuideId(@Param("guideId") Long guideId);

    @Update("UPDATE guides SET views_count = views_count + 1 WHERE id = #{guideId}")
    int incrementViewsCount(@Param("guideId") Long guideId);
}
