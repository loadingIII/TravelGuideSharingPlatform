package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeVO {
    private boolean liked;
    private long likesCount;
}
