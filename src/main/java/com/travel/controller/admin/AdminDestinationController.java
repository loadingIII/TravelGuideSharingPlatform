package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.Destination;
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
@RequestMapping("/admin/destinations")
@RequiredArgsConstructor
public class AdminDestinationController {

    private final DestinationMapper destinationMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = destinationMapper.countAll();
        PageResult<Destination> pageResult = PageResult.of(destinationMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/destination/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("destination", destinationMapper.selectById(id));
        return "admin/destination/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String country,
                         @RequestParam String city,
                         @RequestParam String description,
                         @RequestParam String coverImageUrl,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        destinationMapper.updateDestination(id, name, country, city, description, coverImageUrl);
        logOperation(session, request, "UPDATE", "DESTINATION", id, "修改目的地: " + name);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/destinations";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        Destination dest = destinationMapper.selectById(id);
        destinationMapper.deleteById(id);
        logOperation(session, request, "DELETE", "DESTINATION", id, "删除目的地: " + (dest != null ? dest.getName() : id));
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/destinations";
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
