package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.GuideCommentMapper;
import com.travel.pojo.model.GuideComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final GuideCommentMapper commentMapper;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = commentMapper.countAll();
        PageResult<GuideComment> pageResult = PageResult.of(commentMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/comment/list";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        commentMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/comments";
    }
}
