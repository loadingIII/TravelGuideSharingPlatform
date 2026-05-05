package com.travel.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeVO {
    private List<ShowcaseItemVO> recommendedDestinations;
    private List<ShowcaseItemVO> popularDestinations;
    private List<ShowcaseItemVO> inspirationDestinations;
    private List<GuideListItemVO> communityPicks;
}
