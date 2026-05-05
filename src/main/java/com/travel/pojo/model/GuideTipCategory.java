package com.travel.pojo.model;

import lombok.Data;

@Data
public class GuideTipCategory {
    private Long id;
    private Long guideId;
    private String categoryName;
    private Integer sortOrder;
}
