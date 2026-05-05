package com.travel.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchVO {
    private List<DestinationItemVO> destinations;
    private List<GuideListItemVO> guides;
}
