package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ShowcaseItemVO {
    private Long destinationId;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private BigDecimal rating;
}
