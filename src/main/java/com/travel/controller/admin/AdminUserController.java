package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.UserMapper;
import com.travel.pojo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;

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
                         RedirectAttributes ra) {
        userMapper.updateUser(id, username, phone, email, status);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/users";
    }
}
