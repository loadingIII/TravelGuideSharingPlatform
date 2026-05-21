package com.travel.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteVO {
    private boolean favorited;
    private long favoritesCount;
}
