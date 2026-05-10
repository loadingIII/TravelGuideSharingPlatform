package com.travel.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 审核结果DTO
 * 封装审核动作解析后的状态码和描述信息
 */
@Data
@AllArgsConstructor
public class ReviewResult {
    /** 审核后的状态码 */
    private Integer status;
    /** 审核动作的中文描述 */
    private String detail;
}
