package com.travel.pojo.model;

import lombok.Data;

@Data
public class GuideTipItem {
    private Long id;
    private Long tipId;
    private String itemText;
    private Integer sortOrder;
}
