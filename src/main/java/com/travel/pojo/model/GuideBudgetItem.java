package com.travel.pojo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GuideBudgetItem {
    private Long id;
    private Long guideId;
    private String categoryCode;
    private String categoryName;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Integer sortOrder;
}
