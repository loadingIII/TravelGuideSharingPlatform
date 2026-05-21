package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideFavorite {
    private Long guideId;
    private Long userId;
    private LocalDateTime createdAt;
}
