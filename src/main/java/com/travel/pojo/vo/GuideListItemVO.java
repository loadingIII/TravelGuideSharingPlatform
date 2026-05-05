package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class GuideListItemVO {
    private Long id;
    private Long destinationId;
    private String destinationName;
    private Long authorId;
    private String authorName;
    private String authorAvatarUrl;
    private String title;
    private String summary;
    private String coverImageUrl;
    private String locationText;
    private String scope;
    private String travelMode;
    private Integer days;
    private BigDecimal budgetTotal;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer favoritesCount;
    private LocalDateTime publishedAt;
}
