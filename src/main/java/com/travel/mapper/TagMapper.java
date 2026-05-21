package com.travel.mapper;

import com.travel.pojo.model.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("SELECT id, name FROM tags ORDER BY id")
    List<Tag> selectAll();

    @Select("SELECT t.id, t.name FROM tags t INNER JOIN guide_tags gt ON t.id = gt.tag_id WHERE gt.guide_id = #{guideId}")
    List<Tag> selectByGuideId(@Param("guideId") Long guideId);

    @Insert("INSERT INTO tags (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);

    @Select("SELECT id FROM tags WHERE name = #{name}")
    Long selectIdByName(@Param("name") String name);

    @Insert("INSERT IGNORE INTO guide_tags (guide_id, tag_id) VALUES (#{guideId}, #{tagId})")
    int insertGuideTag(@Param("guideId") Long guideId, @Param("tagId") Long tagId);

    @Delete("DELETE FROM guide_tags WHERE guide_id = #{guideId}")
    int deleteByGuideId(@Param("guideId") Long guideId);

    @Select("SELECT * FROM tags WHERE name = #{name} LIMIT 1")
    Tag findByName(@Param("name") String name);

    @Select("SELECT t.id, t.name FROM tags t INNER JOIN guide_tags gt ON t.id = gt.tag_id GROUP BY t.id ORDER BY COUNT(gt.guide_id) DESC LIMIT 10")
    List<Tag> selectHotTags();
}
