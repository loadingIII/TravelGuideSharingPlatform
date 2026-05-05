package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class GuideItinerarySpot {
    private Long id;
    private Long itineraryDayId;
    private String name;
    private String description;
    private LocalTime visitTime;
    private Integer durationMinutes;
    private String imageUrl;
    private Integer sortOrder;
}
