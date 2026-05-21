package com.travel.service.impl;

import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;
import com.travel.mapper.GuideFavoriteMapper;
import com.travel.pojo.vo.FavoriteVO;
import com.travel.security.UserContext;
import com.travel.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final GuideFavoriteMapper guideFavoriteMapper;

    @Override
    @Transactional
    public FavoriteVO favoriteGuide(Long guideId) {
        Long userId = requireUserId();
        if (guideFavoriteMapper.exists(guideId, userId) == 0) {
            guideFavoriteMapper.insert(guideId, userId);
            guideFavoriteMapper.incrementFavoritesCount(guideId);
        }
        return FavoriteVO.builder()
                .favorited(true)
                .favoritesCount(guideFavoriteMapper.selectFavoritesCount(guideId))
                .build();
    }

    @Override
    @Transactional
    public FavoriteVO unfavoriteGuide(Long guideId) {
        Long userId = requireUserId();
        if (guideFavoriteMapper.exists(guideId, userId) > 0) {
            guideFavoriteMapper.delete(guideId, userId);
            guideFavoriteMapper.decrementFavoritesCount(guideId);
        }
        return FavoriteVO.builder()
                .favorited(false)
                .favoritesCount(guideFavoriteMapper.selectFavoritesCount(guideId))
                .build();
    }

    @Override
    public FavoriteVO getFavoriteStatus(Long guideId) {
        Long userId = UserContext.requireUserId();
        boolean favorited = userId != null && guideFavoriteMapper.exists(guideId, userId) > 0;
        return FavoriteVO.builder()
                .favorited(favorited)
                .favoritesCount(guideFavoriteMapper.selectFavoritesCount(guideId))
                .build();
    }

    private Long requireUserId() {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }
}
