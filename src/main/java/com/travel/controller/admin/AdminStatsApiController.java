package com.travel.controller.admin;

import com.travel.common.ApiResponse;
import com.travel.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class AdminStatsApiController {

    private final UserMapper userMapper;
    private final GuideMapper guideMapper;
    private final GuideStoryMapper storyMapper;
    private final DestinationMapper destinationMapper;
    private final GuideCommentMapper commentMapper;

    @GetMapping
    public ApiResponse<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.countAll(null));
        data.put("guideCount", guideMapper.countAll(null));
        data.put("storyCount", storyMapper.countAll(null));
        data.put("destinationCount", destinationMapper.countAll());
        data.put("commentCount", commentMapper.countAll(null));
        return ApiResponse.success(data);
    }
}
