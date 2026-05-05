package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.model.GuideSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/guides")
@RequiredArgsConstructor
public class AdminGuideController {

    private final GuideMapper guideMapper;

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
                         RedirectAttributes ra) {
        guideMapper.updateGuide(id, title, summary);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/guides";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        guideMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/guides";
    }
}
