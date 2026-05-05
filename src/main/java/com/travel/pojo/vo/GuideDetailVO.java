package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class GuideDetailVO {
    private Long id;
    private Long destinationId;
    private String destinationName;
    private Long authorId;
    private String authorName;
    private String authorAvatarUrl;
    private String title;
    private String summary;
    private String contentHtml;
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
    private List<ItineraryDay> itinerary;
    private List<TipCategory> tips;
    private List<BudgetItem> budgetItems;
    private List<RelatedGuide> relatedGuides;

    @Getter
    @Builder
    public static class ItineraryDay {
        private Long id;
        private Integer dayNo;
        private String title;
        private String summary;
        private List<ItinerarySpot> spots;
    }

    @Getter
    @Builder
    public static class ItinerarySpot {
        private Long id;
        private Long itineraryDayId;
        private String name;
        private String description;
        private LocalTime visitTime;
        private Integer durationMinutes;
        private String imageUrl;
    }

    @Getter
    @Builder
    public static class TipCategory {
        private Long id;
        private String categoryName;
        private List<String> items;
    }

    @Getter
    @Builder
    public static class BudgetItem {
        private Long id;
        private String categoryCode;
        private String categoryName;
        private BigDecimal amount;
        private BigDecimal percentage;
    }

    @Getter
    @Builder
    public static class RelatedGuide {
        private Long id;
        private String title;
        private String coverImageUrl;
        private String destinationName;
    }
}
