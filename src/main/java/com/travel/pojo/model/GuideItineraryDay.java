package com.travel.pojo.model;

import lombok.Data;

@Data
public class GuideItineraryDay {
    private Long id;
    private Long guideId;
    private Integer dayNo;
    private String title;
    private String summary;
    private Integer sortOrder;
}
