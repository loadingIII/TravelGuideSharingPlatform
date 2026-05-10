package com.travel.service;

import com.travel.common.PageResult;
import com.travel.pojo.model.GuideBudgetItem;
import com.travel.pojo.model.GuideSummary;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

public interface AdminGuideService {
    PageResult<GuideSummary> listGuides(int page, Integer status);
    GuideSummary getGuideDetail(Long id);
    Map<String, Object> getItinerary(Long id);
    Map<String, Object> getTips(Long id);
    List<GuideBudgetItem> getBudget(Long id);
    Object getGuideTags(Long id);
    void createGuide(Map<String, Object> body, HttpSession session, HttpServletRequest request);
    void updateGuide(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);
    void updateItinerary(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);
    void updateTips(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);
    void updateBudget(Long id, Map<String, Object> body, HttpSession session, HttpServletRequest request);
    void auditGuide(Long id, String action, HttpSession session, HttpServletRequest request);
    void deleteGuide(Long id, HttpSession session, HttpServletRequest request);
}
