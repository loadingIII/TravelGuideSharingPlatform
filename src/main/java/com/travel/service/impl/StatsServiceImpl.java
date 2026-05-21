package com.travel.service.impl;

import com.travel.mapper.*;
import com.travel.service.StatsService;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserMapper userMapper;
    private final GuideMapper guideMapper;
    private final GuideStoryMapper storyMapper;
    private final DestinationMapper destinationMapper;
    private final GuideCommentMapper commentMapper;
    private final ServletContext servletContext;

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.countAll(null));
        data.put("guideCount", guideMapper.countAll(null));
        data.put("storyCount", storyMapper.countAll(null));
        data.put("destinationCount", destinationMapper.countAll());
        data.put("commentCount", commentMapper.countAll(null));

        Object online = servletContext.getAttribute("onlineUserCount");
        data.put("onlineUserCount", online != null ? online : 0);
        return data;
    }
}
