package com.travel.service;

import java.util.Map;

/**
 * 统计看板服务接口
 * 聚合后台首页所需的平台数据概览
 */
public interface StatsService {

    /**
     * 获取平台统计数据
     * 返回用户数、攻略数、故事数、目的地数、评论数
     *
     * @return Map，key为统计项名称，value为对应数量
     */
    Map<String, Object> stats();
}
