package com.travel.service;

import com.travel.common.PageResult;
import com.travel.pojo.vo.GuideStoryVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

public interface AdminStoryService {
    PageResult<GuideStoryVO> listStories(int page, Integer status);
    GuideStoryVO getStoryDetail(Long id);
    void createStory(Map<String, Object> body, HttpSession session, HttpServletRequest request);
    void updateStory(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);
    void auditStory(Long id, String action, HttpSession session, HttpServletRequest request);
    void deleteStory(Long id, HttpSession session, HttpServletRequest request);
}
