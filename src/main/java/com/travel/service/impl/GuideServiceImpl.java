package com.travel.service.impl;

import com.travel.common.PageResult;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ErrorCode;

import com.travel.pojo.dto.CreateGuideDTO;
import com.travel.pojo.dto.UpdateGuideDTO;
import com.travel.pojo.model.GuideDetail;
import com.travel.pojo.model.GuideItineraryDay;
import com.travel.pojo.model.GuideSummary;
import com.travel.pojo.model.UserProfile;
import com.travel.pojo.model.Destination;
import com.travel.mapper.GuideMapper;
import com.travel.mapper.UserProfileMapper;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.vo.GuideDetailVO;
import com.travel.pojo.vo.GuideListItemVO;
import com.travel.security.UserContext;
import com.travel.service.GuideService;
import com.travel.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {
    private final GuideMapper guideMapper;
    private final UserProfileMapper userProfileMapper;
    private final DestinationMapper destinationMapper;
    private final TagService tagService;

    @Override
    public GuideDetailVO getGuideDetail(Long guideId) {
        GuideDetail detail = guideMapper.selectGuideDetail(guideId);
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "攻略不存在");
        }

        List<GuideItineraryDay> itineraryDays = guideMapper.listItineraryDays(guideId);

        return GuideDetailVO.builder()
                .id(detail.getId())
                .destinationId(detail.getDestinationId())
                .destinationName(detail.getDestinationName())
                .authorId(detail.getAuthorId())
                .authorName(detail.getAuthorName())
                .authorAvatarUrl(detail.getAuthorAvatarUrl())
                .title(detail.getTitle())
                .summary(detail.getSummary())
                .contentHtml(detail.getContentHtml())
                .coverImageUrl(detail.getCoverImageUrl())
                .locationText(detail.getLocationText())
                .scope(detail.getScope())
                .travelMode(detail.getTravelMode())
                .days(detail.getDays())
                .budgetTotal(detail.getBudgetTotal())
                .viewsCount(detail.getViewsCount())
                .likesCount(detail.getLikesCount())
                .commentsCount(detail.getCommentsCount())
                .favoritesCount(detail.getFavoritesCount())
                .publishedAt(detail.getPublishedAt())
                .itinerary(itineraryDays.stream().map(this::toItineraryDay).toList())
                .build();
    }

    private GuideDetailVO.ItineraryDay toItineraryDay(GuideItineraryDay day) {
        return GuideDetailVO.ItineraryDay.builder()
                .id(day.getId())
                .dayNo(day.getDayNo())
                .title(day.getTitle())
                .summary(day.getSummary())
                .build();
    }

    @Override
    public PageResult<GuideListItemVO> listGuides(String keyword, String scope, String travelMode, String sort, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        List<GuideListItemVO> list = guideMapper.listGuides(keyword, scope, travelMode, sort, offset, safePageSize)
                .stream().map(this::toGuideListItem).toList();
        long total = guideMapper.countGuides(keyword, scope, travelMode);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    @Override
    public PageResult<GuideListItemVO> listGuidesByDestination(Long destinationId, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        List<GuideListItemVO> list = guideMapper.listGuidesByDestination(destinationId, offset, safePageSize)
                .stream().map(this::toGuideListItem).toList();
        long total = guideMapper.countGuidesByDestination(destinationId);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    @Override
    public List<GuideListItemVO> getTopGuidesByLikes(int limit) {
        return guideMapper.listTopGuidesByLikes(limit).stream().map(this::toGuideListItem).toList();
    }

    @Override
    public List<GuideListItemVO> listCommunityPicks(int limit) {
        return guideMapper.listCommunityPicks(limit).stream().map(this::toGuideListItem).toList();
    }

    @Override
    public List<GuideListItemVO> searchGuides(String query, int limit) {
        return guideMapper.searchGuides(query, limit).stream().map(this::toGuideListItem).toList();
    }

    @Override
    public PageResult<GuideListItemVO> listMyGuides(Integer page, Integer pageSize) {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        int offset = (safePage - 1) * safePageSize;
        List<GuideListItemVO> list = guideMapper.selectByAuthorId(userId, offset, safePageSize)
                .stream()
                .map(this::toGuideListItem)
                .toList();
        long total = guideMapper.countByAuthorId(userId);
        return PageResult.of(list, safePage, safePageSize, total);
    }

    private GuideListItemVO toGuideListItem(GuideSummary entity) {
        return GuideListItemVO.builder()
                .id(entity.getId())
                .destinationId(entity.getDestinationId())
                .destinationName(entity.getDestinationName())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorAvatarUrl(entity.getAuthorAvatarUrl())
                .title(entity.getTitle())
                .summary(entity.getSummary())
                .coverImageUrl(entity.getCoverImageUrl())
                .locationText(entity.getLocationText())
                .scope(entity.getScope())
                .travelMode(entity.getTravelMode())
                .days(entity.getDays())
                .budgetTotal(entity.getBudgetTotal())
                .viewsCount(entity.getViewsCount())
                .likesCount(entity.getLikesCount())
                .commentsCount(entity.getCommentsCount())
                .favoritesCount(entity.getFavoritesCount())
                .publishedAt(entity.getPublishedAt())
                .build();
    }

    private int normalizePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) return 10;
        return Math.min(pageSize, 50);
    }

    @Override
    @Transactional
    public Long createGuide(CreateGuideDTO dto) {
        Long authorId = UserContext.requireUserId();
        if (authorId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        Long destinationId = dto.getDestinationId();
        if (destinationId == null && dto.getDestinationName() != null && !dto.getDestinationName().trim().isEmpty()) {
            String destName = dto.getDestinationName().trim();
            Destination destination = destinationMapper.findByName(destName);
            if (destination == null) {
                destination = new Destination();
                destination.setName(destName);
                destination.setCountry("");
                destination.setCity(destName);
                destination.setDescription("");
                destinationMapper.insert(destination);
            }
            destinationId = destination.getId();
        }

        GuideSummary guide = new GuideSummary();
        guide.setDestinationId(destinationId);
        guide.setAuthorId(authorId);
        guide.setTitle(dto.getTitle());
        guide.setSummary(dto.getSummary());
        guide.setContentHtml(dto.getContentHtml());
        guide.setCoverImageUrl(dto.getCoverImageUrl());
        guide.setLocationText(dto.getLocationText());
        guide.setScope(dto.getScope());
        guide.setTravelMode(dto.getTravelMode());
        guide.setDays(dto.getItineraryDays() != null ? dto.getItineraryDays().size() : 0);
        guide.setBudgetTotal(BigDecimal.ZERO);
        guide.setPublishedAt(LocalDateTime.now());
        guide.setStatus(0);

        guideMapper.insertFull(guide);
        Long guideId = guide.getId();

        if (dto.getItineraryDays() != null) {
            int daySort = 1;
            for (CreateGuideDTO.ItineraryDayDTO dayDTO : dto.getItineraryDays()) {
                GuideItineraryDay day = new GuideItineraryDay();
                day.setGuideId(guideId);
                day.setDayNo(dayDTO.getDayNo());
                day.setTitle(dayDTO.getTitle());
                day.setSummary(dayDTO.getSummary());
                day.setSortOrder(daySort++);
                guideMapper.insertItineraryDay(day);
            }
        }

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            tagService.syncGuideTags(guideId, dto.getTagIds());
        } else if (dto.getTagNames() != null && !dto.getTagNames().isEmpty()) {
            tagService.syncGuideTagsByName(guideId, dto.getTagNames());
        }

        UserProfile profile = userProfileMapper.selectByUserId(authorId);
        if (profile != null) {
            profile.setGuidesCount(profile.getGuidesCount() + 1);
            userProfileMapper.updateProfile(profile);
        }

        return guideId;
    }

    @Override
    @Transactional
    public void updateGuide(Long guideId, UpdateGuideDTO dto) {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        GuideSummary existing = guideMapper.selectById(guideId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "攻略不存在");
        }
        if (!userId.equals(existing.getAuthorId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权编辑此攻略");
        }

        guideMapper.updateGuideFull(guideId, dto.getTitle(), dto.getSummary(),
                dto.getContentHtml(), dto.getCoverImageUrl(), dto.getLocationText(),
                dto.getScope(), dto.getTravelMode());

        if (dto.getTagIds() != null) {
            tagService.syncGuideTags(guideId, dto.getTagIds());
        }
    }

    @Override
    @Transactional
    public void deleteGuide(Long guideId) {
        Long userId = UserContext.requireUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        GuideSummary existing = guideMapper.selectById(guideId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "攻略不存在");
        }
        if (!userId.equals(existing.getAuthorId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除此攻略");
        }

        guideMapper.deleteItineraryDaysByGuideId(guideId);
        tagService.syncGuideTags(guideId, null);
        guideMapper.deleteById(guideId);

        UserProfile profile = userProfileMapper.selectByUserId(userId);
        if (profile != null) {
            profile.setGuidesCount(Math.max(0, profile.getGuidesCount() - 1));
            userProfileMapper.updateProfile(profile);
        }
    }
}
