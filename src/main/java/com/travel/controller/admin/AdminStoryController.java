package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideStoryMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/stories")
@RequiredArgsConstructor
public class AdminStoryController {

    private final GuideStoryMapper storyMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyMapper.countAll();
        PageResult<GuideStoryVO> pageResult = PageResult.of(storyMapper.selectAll(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/story/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("story", storyMapper.selectById(id));
        return "admin/story/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String content,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        storyMapper.updateContent(id, content);
        logOperation(session, request, "UPDATE", "STORY", id, "修改故事");
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/stories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        storyMapper.deleteById(id);
        logOperation(session, request, "DELETE", "STORY", id, "删除故事");
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/stories";
    }

    private void logOperation(HttpSession session, HttpServletRequest request,
                              String action, String targetType, Long targetId, String detail) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) return;
        AdminLog log = new AdminLog();
        log.setAdminId((Long) admin.get("id"));
        log.setAdminUsername((String) admin.get("username"));
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(log);
    }
}
