package com.travel.service;

import com.travel.pojo.dto.ReviewResult;

public interface ReviewService {
    ReviewResult resolveAction(String action, String... customActions);
}
