package com.travel.mapper;

import com.travel.pojo.model.Destination;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 目的地Mapper接口
 */
@Mapper
public interface DestinationMapper {

    /**
     * 获取目的地列表
     */
    List<Destination> listDestinations(@Param("keyword") String keyword,
                                             @Param("hotSort") Boolean hotSort,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    /**
     * 统计目的地数量
     */
    long countDestinations(@Param("keyword") String keyword);

    /**
     * 根据ID获取目的地
     */
    Destination selectById(@Param("id") Long id);

    /**
     * 搜索目的地
     */
    List<Destination> searchDestinations(@Param("query") String query, @Param("limit") int limit);

    /**
     * 获取热度前N的目的地
     */
    List<Destination> listHotDestinations(@Param("limit") int limit);

    @Select("SELECT * FROM destinations ORDER BY popularity_score DESC LIMIT #{offset}, #{pageSize}")
    List<Destination> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM destinations")
    long countAll();

    @Update("UPDATE destinations SET name=#{name}, country=#{country}, city=#{city}, description=#{description}, cover_image_url=#{coverImageUrl} WHERE id=#{id}")
    int updateDestination(@Param("id") Long id, @Param("name") String name,
                          @Param("country") String country, @Param("city") String city,
                          @Param("description") String description,
                          @Param("coverImageUrl") String coverImageUrl);

    @Delete("DELETE FROM destinations WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Insert("INSERT INTO destinations (name, country, city, description, cover_image_url) VALUES (#{name}, #{country}, #{city}, #{description}, #{coverImageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Destination destination);

    /**
     * 根据名称查找目的地
     */
    @Select("SELECT * FROM destinations WHERE name = #{name} LIMIT 1")
    Destination findByName(@Param("name") String name);
}
