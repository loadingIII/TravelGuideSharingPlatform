package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.GuideSummary;
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
@RequestMapping("/admin/guides")
@RequiredArgsConstructor
public class AdminGuideController {

    private final GuideMapper guideMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = guideMapper.countAll();
        PageResult<GuideSummary> pageResult = PageResult.of(guideMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/guide/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("guide", guideMapper.selectById(id));
        return "admin/guide/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String summary,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        guideMapper.updateGuide(id, title, summary);
        logOperation(session, request, "UPDATE", "GUIDE", id, "修改攻略: " + title);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/guides";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        GuideSummary guide = guideMapper.selectById(id);
        guideMapper.deleteById(id);
        logOperation(session, request, "DELETE", "GUIDE", id, "删除攻略: " + (guide != null ? guide.getTitle() : id));
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/guides";
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
