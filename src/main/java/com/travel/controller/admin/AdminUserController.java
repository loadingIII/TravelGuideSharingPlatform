package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.UserMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.User;
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
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll();
        PageResult<User> pageResult = PageResult.of(userMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/user/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userMapper.selectById(id));
        return "admin/user/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String username,
                         @RequestParam String phone,
                         @RequestParam String email,
                         @RequestParam Integer status,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        userMapper.updateUser(id, username, phone, email, status);
        logOperation(session, request, "UPDATE", "USER", id, "修改用户: " + username);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra,
                         HttpSession session,
                         HttpServletRequest request) {
        User user = userMapper.selectById(id);
        userMapper.deleteById(id);
        logOperation(session, request, "DELETE", "USER", id, "删除用户: " + (user != null ? user.getUsername() : id));
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/users";
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
