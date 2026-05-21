package com.travel.pojo.model;

import lombok.Data;

@Data
public class GuideItinerarySpot {
    private Long id;
    private Long guideId;
    private Integer dayNo;
    private String name;
    private String description;
    private String imageUrl;
    private String time;
    private String duration;
    private Integer sortOrder;
}
