package com.travel.service;

import com.travel.pojo.dto.ReviewResult;

/**
 * 内容审核服务接口
 * 统一处理后台各类内容的审核状态转换逻辑
 */
public interface ReviewService {

    /**
     * 解析审核动作，返回对应的状态和描述
     * <p>
     * 默认动作：approve->通过(1)、reject->拒绝(2)、down->下架(3)
     * 自定义动作需通过 varargs 传入（如 disable/enable）
     * </p>
     *
     * @param action        审核动作标识
     * @param customActions 可选的自定义动作名称列表
     * @return 审核结果（status 状态码 + detail 描述），无法识别的动作返回 null
     */
    ReviewResult resolveAction(String action, String... customActions);
}
