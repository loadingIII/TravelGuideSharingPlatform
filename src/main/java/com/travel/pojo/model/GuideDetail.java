package com.travel.pojo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GuideDetail {
    private Long id;
    private Long destinationId;
    private Long authorId;
    private String title;
    private String summary;
    private String contentHtml;
    private String coverImageUrl;
    private String locationText;
    private String scope;
    private String travelMode;
    private LocalDateTime publishedAt;
    private Integer days;
    private BigDecimal budgetTotal;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer favoritesCount;
    private String destinationName;
    private String authorName;
    private String authorAvatarUrl;
    private Integer status;
}
