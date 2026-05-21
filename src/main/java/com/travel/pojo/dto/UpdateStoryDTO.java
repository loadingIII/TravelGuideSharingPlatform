package com.travel.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateStoryDTO {
    private String content;
    private List<String> imageUrls;
}
