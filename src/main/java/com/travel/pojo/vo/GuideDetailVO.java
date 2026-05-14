package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private String authorLevel;
    private Integer authorGuidesCount;
    private Integer authorFollowersCount;
    private Integer authorLikedCount;
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
    private List<GuideListItemVO> relatedGuides;

    @Getter
    @Builder
    public static class ItineraryDay {
        private Long id;
        private Integer dayNo;
        private String title;
        private String summary;
        private List<Spot> spots;

        @Getter
        @Builder
        public static class Spot {
            private Long id;
            private String name;
            private String description;
            private String imageUrl;
            private String time;
            private String duration;
        }
    }
}
