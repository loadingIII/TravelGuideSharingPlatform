package com.travel.mapper;

import com.travel.pojo.vo.GuideStoryVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 旅行者故事Mapper接口
 */
@Mapper
public interface GuideStoryMapper {

    /**
     * 根据ID查询故事
     *
     * @param id 故事ID
     * @return 故事详情
     */
    GuideStoryVO selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询故事列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 故事列表
     */
    List<GuideStoryVO> selectByUserId(@Param("userId") Long userId,
                                       @Param("offset") int offset,
                                       @Param("pageSize") int pageSize);

    /**
     * 统计用户的故事数量
     *
     * @param userId 用户ID
     * @return 故事数量
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 分页查询故事列表
     *
     * @param offset   偏移量
     * @param pageSize 每页数量
     * @return 故事列表
     */
    List<GuideStoryVO> selectAll(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize,
                                  @Param("status") Integer status);

    /**
     * 统计所有故事数量
     *
     * @return 故事数量
     */
    long countAll(@Param("status") Integer status);

    @Delete("DELETE FROM traveler_stories WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE traveler_stories SET content=#{content} WHERE id=#{id}")
    int updateContent(@Param("id") Long id, @Param("content") String content);

    @Update("UPDATE traveler_stories SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Insert("INSERT INTO traveler_stories (author_user_id, author_name, author_avatar_url, is_vip, content, status, is_deleted) VALUES (#{authorUserId}, #{authorName}, #{authorAvatarUrl}, #{isVip}, #{content}, #{status}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GuideStoryVO story);

    @Update("UPDATE traveler_stories SET is_deleted = 1 WHERE id = #{id}")
    int softDeleteById(@Param("id") Long id);

    @Update("UPDATE traveler_stories SET content=#{content} WHERE id=#{id} AND author_user_id=#{authorUserId}")
    int updateContentByAuthor(@Param("id") Long id, @Param("content") String content, @Param("authorUserId") Long authorUserId);

    @Select("SELECT author_user_id FROM traveler_stories WHERE id = #{id}")
    Long selectAuthorUserId(@Param("id") Long id);

    @Insert("INSERT INTO traveler_story_images (story_id, image_url, sort_order) VALUES (#{storyId}, #{imageUrl}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertStoryImage(@Param("storyId") Long storyId, @Param("imageUrl") String imageUrl, @Param("sortOrder") int sortOrder);

    @Select("SELECT image_url FROM traveler_story_images WHERE story_id = #{storyId} ORDER BY sort_order")
    List<String> selectImageUrlsByStoryId(@Param("storyId") Long storyId);

    @Delete("DELETE FROM traveler_story_images WHERE story_id = #{storyId}")
    int deleteImagesByStoryId(@Param("storyId") Long storyId);

    @Update("UPDATE traveler_stories SET comments_count = (SELECT COUNT(*) FROM traveler_story_comments c WHERE c.story_id = traveler_stories.id AND c.is_deleted = 0)")
    int correctCommentsCount();

    @Update("UPDATE traveler_stories SET likes_count = (SELECT COUNT(*) FROM traveler_story_likes sl WHERE sl.story_id = traveler_stories.id)")
    int correctLikesCount();
}
