package com.travel.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateGuideDTO {
    private String title;
    private String summary;
    private String contentHtml;
    private String coverImageUrl;
    private String locationText;
    private String scope;
    private String travelMode;
    private Integer days;
    private List<Long> tagIds;
    private List<String> tagNames;
    private List<CreateGuideDTO.ItineraryDayDTO> itineraryDays;
}
