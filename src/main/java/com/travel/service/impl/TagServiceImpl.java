package com.travel.service.impl;

import com.travel.mapper.TagMapper;
import com.travel.pojo.model.Tag;
import com.travel.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    public List<Tag> listAllTags() {
        return tagMapper.selectAll();
    }

    @Override
    public List<Tag> getTagsByGuideId(Long guideId) {
        return tagMapper.selectByGuideId(guideId);
    }

    @Override
    @Transactional
    public void syncGuideTags(Long guideId, List<Long> tagIds) {
        tagMapper.deleteByGuideId(guideId);
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                tagMapper.insertGuideTag(guideId, tagId);
            }
        }
    }

    @Override
    @Transactional
    public void syncGuideTagsByName(Long guideId, List<String> tagNames) {
        tagMapper.deleteByGuideId(guideId);
        if (tagNames != null) {
            for (String tagName : tagNames) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Long tagId = findOrCreateTagByName(tagName.trim());
                    tagMapper.insertGuideTag(guideId, tagId);
                }
            }
        }
    }

    @Override
    public List<Tag> listHotTags() {
        return tagMapper.selectHotTags();
    }

    @Override
    public Long findOrCreateTagByName(String tagName) {
        Tag tag = tagMapper.findByName(tagName);
        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName);
            tagMapper.insert(tag);
        }
        return tag.getId();
    }
}
