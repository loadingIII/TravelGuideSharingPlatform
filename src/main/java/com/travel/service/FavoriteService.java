package com.travel.service;

import com.travel.pojo.vo.FavoriteVO;

public interface FavoriteService {
    FavoriteVO favoriteGuide(Long guideId);
    FavoriteVO unfavoriteGuide(Long guideId);
    FavoriteVO getFavoriteStatus(Long guideId);
}
