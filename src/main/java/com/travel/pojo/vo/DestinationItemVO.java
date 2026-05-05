package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DestinationItemVO {
    private Long id;
    private String name;
    private String country;
    private String city;
    private String description;
    private String coverImageUrl;
    private BigDecimal rating;
    private Integer guidesCount;
    private Integer travelersCount;
    private BigDecimal popularityScore;
}
