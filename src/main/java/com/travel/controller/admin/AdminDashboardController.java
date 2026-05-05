package com.travel.controller.admin;

import com.travel.mapper.DestinationMapper;
import com.travel.mapper.GuideCommentMapper;
import com.travel.mapper.GuideMapper;
import com.travel.mapper.GuideStoryMapper;
import com.travel.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserMapper userMapper;
    private final GuideMapper guideMapper;
    private final GuideStoryMapper storyMapper;
    private final DestinationMapper destinationMapper;
    private final GuideCommentMapper commentMapper;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userMapper.countAll());
        model.addAttribute("guideCount", guideMapper.countAll());
        model.addAttribute("storyCount", storyMapper.countAll());
        model.addAttribute("destinationCount", destinationMapper.countAll());
        model.addAttribute("commentCount", commentMapper.countAll());
        return "admin/dashboard";
    }
}
