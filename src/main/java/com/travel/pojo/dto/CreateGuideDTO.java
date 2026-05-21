package com.travel.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateGuideDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String summary;
    private String contentHtml;
    private Long destinationId;
    @NotBlank(message = "目的地不能为空", groups = {})
    private String destinationName;
    private String scope;
    private String travelMode;
    private String coverImageUrl;
    private String locationText;
    private BigDecimal budgetTotal;
    private List<ItineraryDayDTO> itineraryDays;
    private List<Long> tagIds;
    private List<String> tagNames;

    @Data
    public static class ItineraryDayDTO {
        private Integer dayNo;
        private String title;
        private String summary;
        private List<SpotDTO> spots;
    }

    @Data
    public static class SpotDTO {
        private String name;
        private String description;
        private String imageUrl;
        private String time;
        private String duration;
    }
}
