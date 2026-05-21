package com.travel.service;

import com.travel.pojo.model.Tag;

import java.util.List;

public interface TagService {
    List<Tag> listAllTags();
    List<Tag> getTagsByGuideId(Long guideId);
    void syncGuideTags(Long guideId, List<Long> tagIds);
    void syncGuideTagsByName(Long guideId, List<String> tagNames);
    Long findOrCreateTagByName(String tagName);
    List<Tag> listHotTags();
}
