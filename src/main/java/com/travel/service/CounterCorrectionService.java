package com.travel.service;

import com.travel.mapper.GuideMapper;
import com.travel.mapper.GuideStoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounterCorrectionService {

    private final GuideMapper guideMapper;
    private final GuideStoryMapper storyMapper;

    /**
     * 每天凌晨3点校正计数器
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void correctAllCounters() {
        log.info("开始校正计数器...");
        correctGuideCommentsCount();
        correctStoryCommentsCount();
        correctGuideLikesCount();
        correctStoryLikesCount();
        correctGuideFavoritesCount();
        log.info("计数器校正完成");
    }

    private void correctGuideCommentsCount() {
        int corrected = guideMapper.correctCommentsCount();
        log.info("校正攻略评论计数: {}条", corrected);
    }

    private void correctStoryCommentsCount() {
        int corrected = storyMapper.correctCommentsCount();
        log.info("校正故事评论计数: {}条", corrected);
    }

    private void correctGuideLikesCount() {
        int corrected = guideMapper.correctLikesCount();
        log.info("校正攻略点赞计数: {}条", corrected);
    }

    private void correctStoryLikesCount() {
        int corrected = storyMapper.correctLikesCount();
        log.info("校正故事点赞计数: {}条", corrected);
    }

    private void correctGuideFavoritesCount() {
        int corrected = guideMapper.correctFavoritesCount();
        log.info("校正攻略收藏计数: {}条", corrected);
    }
}
