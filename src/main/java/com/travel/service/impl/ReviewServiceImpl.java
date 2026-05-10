package com.travel.service.impl;

import com.travel.pojo.dto.ReviewResult;
import com.travel.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final Map<String, ReviewResult> defaultActions = new HashMap<>();
    private final Map<String, ReviewResult> customActions = new HashMap<>();

    public ReviewServiceImpl() {
        // 默认审核动作
        defaultActions.put("approve", new ReviewResult(1, "审核通过"));
        defaultActions.put("reject", new ReviewResult(2, "审核拒绝"));
        defaultActions.put("down", new ReviewResult(3, "下架"));

        // 用户审核自定义动作
        customActions.put("disable", new ReviewResult(0, "禁用"));
        customActions.put("enable", new ReviewResult(1, "启用"));
    }

    @Override
    public ReviewResult resolveAction(String action, String... customActions) {
        // 先查默认动作
        ReviewResult result = defaultActions.get(action);
        if (result != null) return result;

        // 再查自定义动作
        if (customActions != null) {
            for (String ca : customActions) {
                ReviewResult cr = this.customActions.get(ca);
                if (ca.equals(action) && cr != null) return cr;
            }
        }

        return null;
    }
}
