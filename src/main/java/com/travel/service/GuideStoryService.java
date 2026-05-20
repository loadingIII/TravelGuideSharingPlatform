package com.travel.service;

import com.travel.pojo.common.PageResult;
import com.travel.pojo.dto.CreateCommentDTO;
import com.travel.pojo.dto.CreateStoryDTO;
import com.travel.pojo.dto.UpdateStoryDTO;
import com.travel.pojo.model.StoryComment;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.pojo.vo.StoryCommentVO;

import java.util.List;

public interface GuideStoryService {

    GuideStoryVO getStoryById(Long id);

    PageResult<GuideStoryVO> getStoriesByUserId(Long userId, Integer page, Integer pageSize);

    PageResult<GuideStoryVO> listStories(Integer page, Integer pageSize);

    Long createStory(CreateStoryDTO dto);

    void updateStory(Long storyId, UpdateStoryDTO dto);

    void deleteStory(Long storyId);

    PageResult<GuideStoryVO> listMyStories(Integer page, Integer pageSize);

    Long addStoryComment(Long storyId, CreateCommentDTO dto);

    PageResult<StoryComment> listStoryComments(Long storyId, Integer page, Integer pageSize);

    List<StoryCommentVO> listStoryCommentsTree(Long storyId);

    void deleteStoryComment(Long commentId);
}
