package com.travel.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResult {
    private Integer status;
    private String detail;
}
