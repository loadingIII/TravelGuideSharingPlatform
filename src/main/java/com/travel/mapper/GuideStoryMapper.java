package com.travel.mapper;

import com.travel.pojo.vo.GuideStoryVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
}
