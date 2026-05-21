package com.travel.mapper;

import com.travel.pojo.model.GuideItinerarySpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideItinerarySpotMapper {

    List<GuideItinerarySpot> listByGuideId(@Param("guideId") Long guideId);

    List<GuideItinerarySpot> listByGuideIdAndDayNo(@Param("guideId") Long guideId, @Param("dayNo") Integer dayNo);

    int batchInsert(@Param("list") List<GuideItinerarySpot> list);

    int deleteByGuideId(@Param("guideId") Long guideId);
}
