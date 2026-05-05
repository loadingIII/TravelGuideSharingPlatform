package com.travel.pojo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DestinationShowcase {
    private Long id;
    private Long destinationId;
    private String section;
    private String displayTitle;
    private String displaySubtitle;
    private String displayDescription;
    private String displayImageUrl;
    private BigDecimal displayRating;
    private Integer sortOrder;
    private Boolean isActive;
}
