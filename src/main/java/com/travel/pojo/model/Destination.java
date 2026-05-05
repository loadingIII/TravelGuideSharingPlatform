package com.travel.pojo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Destination {
    private Long id;
    private String name;
    private String country;
    private String city;
    private String description;
    private String coverImageUrl;
    private BigDecimal ratingAvg;
    private Integer guidesCount;
    private Integer travelersCount;
    private BigDecimal popularityScore;
}
