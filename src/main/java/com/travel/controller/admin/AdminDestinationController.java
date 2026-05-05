package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.model.Destination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/destinations")
@RequiredArgsConstructor
public class AdminDestinationController {

    private final DestinationMapper destinationMapper;

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
                         RedirectAttributes ra) {
        destinationMapper.updateDestination(id, name, country, city, description, coverImageUrl);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/destinations";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        destinationMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/destinations";
    }
}
