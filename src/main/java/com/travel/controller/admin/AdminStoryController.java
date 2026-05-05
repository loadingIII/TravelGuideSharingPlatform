package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.GuideStoryMapper;
import com.travel.pojo.vo.GuideStoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/stories")
@RequiredArgsConstructor
public class AdminStoryController {

    private final GuideStoryMapper storyMapper;

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
                         RedirectAttributes ra) {
        storyMapper.updateContent(id, content);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/stories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        storyMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/stories";
    }
}
