package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowVO {
    private boolean following;
    private long followersCount;
}
